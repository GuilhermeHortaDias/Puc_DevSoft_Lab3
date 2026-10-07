"""Verifica artefatos documentais. Nao testa uma aplicacao nem valida semantica UML."""

import hashlib
import json
import re
import struct
import sys
import xml.etree.ElementTree as ET
from pathlib import Path
from urllib.parse import unquote, urlsplit

ROOT = Path(__file__).resolve().parents[1]
ERRORS = []


def check(condition, message):
    if not condition:
        ERRORS.append(message)


def main():
    markdown_files = sorted(ROOT.rglob('*.md'))
    texts = {path: path.read_text(encoding='utf-8-sig') for path in markdown_files}
    checked_links = 0
    for path, text in texts.items():
        for target in re.findall(r'!?\[[^\]]*\]\(([^)]+)\)', text):
            target = target.strip().strip('<>')
            if urlsplit(target).scheme or target.startswith('#'):
                continue
            link_path = unquote(target.split('#', 1)[0])
            check((path.parent / link_path).is_file(), f'Link ausente em {path.relative_to(ROOT)}: {target}')
            checked_links += 1

    analysis = texts[ROOT / 'docs/analise-enunciado.md']
    stories = texts[ROOT / 'docs/modelagem/historias-do-usuario.md']
    usecases = texts[ROOT / 'docs/modelagem/casos-de-uso.md']
    matrix = texts[ROOT / 'docs/modelagem/rastreabilidade.md']
    registries = {
        'RF': set(re.findall(r'^\| (RF\d{2}) \|', analysis, re.M)),
        'RN': set(re.findall(r'^\| (RN\d{2}) \|', analysis, re.M)),
        'D': set(re.findall(r'^\| (D\d{2}) \|', analysis, re.M)),
        'HU': set(re.findall(r'^## (HU\d{2}) ', stories, re.M)),
        'UC': set(re.findall(r'^## (UC\d{2}) ', usecases, re.M)),
    }
    expected_counts = {'RF': 16, 'RN': 8, 'D': 11, 'HU': 14, 'UC': 12}
    for prefix, registry in registries.items():
        check(len(registry) == expected_counts[prefix], f'Quantidade de {prefix} incorreta: {len(registry)}')
        for path, text in texts.items():
            refs = set(re.findall(rf'\b{prefix}\d{{2}}\b', text))
            for unknown in sorted(refs - registry):
                ERRORS.append(f'Identificador inexistente em {path.relative_to(ROOT)}: {unknown}')

    matrix_requirements = set(re.findall(r'^\| (RF\d{2}) \|', matrix, re.M))
    check(matrix_requirements == registries['RF'], 'Cobertura de requisitos incompleta na matriz.')
    matrix_rules = set(re.findall(r'^\| (RN\d{2}) \|', matrix, re.M))
    check(matrix_rules == registries['RN'], 'Cobertura de regras incompleta na matriz.')

    diagram_dir = ROOT / 'docs/diagramas'
    manifest = json.loads((diagram_dir / 'manifesto.json').read_text(encoding='utf-8-sig'))
    names = {'casos-de-uso', 'classes', 'componentes'}
    check({entry['name'] for entry in manifest['diagrams']} == names, 'Manifesto deve conter os tres diagramas.')
    for entry in manifest['diagrams']:
        for asset in entry['assets']:
            path = ROOT / asset['path']
            check(path.is_file(), f'Artefato ausente: {asset["path"]}')
            if path.is_file():
                digest = hashlib.sha256(path.read_bytes()).hexdigest()
                check(digest == asset['sha256'], f'Arquivo alterado desde a renderizacao: {asset["path"]}')

    for name in sorted(names):
        source = (diagram_dir / f'{name}.puml').read_text(encoding='utf-8')
        check(source.count('@startuml') == 1 and source.count('@enduml') == 1, f'Fonte incompleta: {name}')
        svg = ET.parse(diagram_dir / f'{name}.svg').getroot()
        check(svg.tag.endswith('svg'), f'SVG invalido: {name}')
        svg_text = ' '.join(svg.itertext())
        check('Syntax Error' not in svg_text and 'Assumed diagram type:' not in svg_text, f'Imagem de erro: {name}')
        png = (diagram_dir / f'{name}.png').read_bytes()
        check(png[:8] == b'\x89PNG\r\n\x1a\n', f'PNG invalido: {name}')
        width, height = struct.unpack('>II', png[16:24])
        check(width > 100 and height > 100, f'PNG pequeno/incompleto: {name}')
        print(f'{name}: PNG {width}x{height}; fonte, SVG e hashes conferidos')

    uml_ids = set(re.findall(r'usecase "(UC\d{2})', (diagram_dir / 'casos-de-uso.puml').read_text(encoding='utf-8')))
    check(uml_ids == registries['UC'], 'Casos de uso textuais e diagramados divergem.')

    if ERRORS:
        print('\n'.join(ERRORS), file=sys.stderr)
        return 1
    print(f'OK: {len(markdown_files)} documentos Markdown; {checked_links} links locais; '
          '16 requisitos, 8 regras, 14 historias, 12 casos; 3 diagramas e 6 exportacoes.')
    print('Semantica, layout e fidelidade ao PDF exigem revisao manual complementar.')
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
