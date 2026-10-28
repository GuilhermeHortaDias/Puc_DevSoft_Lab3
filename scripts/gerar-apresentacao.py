"""Gera PPTX editável e PDF. Dependências: python-pptx e reportlab<5 (Python 3.9)."""
from pathlib import Path
from pptx import Presentation
from pptx.util import Pt
from pptx.dml.color import RGBColor
from pptx.enum.shapes import MSO_SHAPE
from reportlab.pdfgen import canvas
from reportlab.lib.colors import HexColor
import textwrap
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
FONT_DIR = Path("/System/Library/Fonts/Supplemental")
PDF_FONT, PDF_BOLD = "Helvetica", "Helvetica-Bold"
if (FONT_DIR / "Arial.ttf").exists():
    pdfmetrics.registerFont(TTFont("Arial", str(FONT_DIR / "Arial.ttf")))
    pdfmetrics.registerFont(TTFont("Arial-Bold", str(FONT_DIR / "Arial Bold.ttf")))
    PDF_FONT, PDF_BOLD = "Arial", "Arial-Bold"

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / 'docs/apresentacao'
OUT.mkdir(exist_ok=True)
prs = Presentation()
prs.slide_width, prs.slide_height = Pt(960), Pt(540)
pdf = canvas.Canvas(str(OUT / 'Sistema-de-Moeda-Estudantil.pdf'), pagesize=(960,540))
pdf.setTitle('Sistema de Moeda Estudantil — Laboratório 3')
BG, WHITE, MUTED, ACCENT, PANEL = '101D2C','F4F7FA','B6C7D9','56D9B0','1B2D42'
slide = None

def box(x,y,w,h,color):
    sh=slide.shapes.add_shape(MSO_SHAPE.RECTANGLE,Pt(x),Pt(y),Pt(w),Pt(h))
    sh.fill.solid(); sh.fill.fore_color.rgb=RGBColor.from_string(color); sh.line.fill.background()
    pdf.setFillColor(HexColor('#'+color)); pdf.rect(x,540-y-h,w,h,fill=1,stroke=0)

def txt(x,y,w,text,size=20,color=WHITE,bold=False):
    # Explicit wrapping keeps both exports aligned and every element editable.
    lines=[]
    for line in text.split('\n'):
        lines.extend(textwrap.wrap(line,width=max(1,int(w/(size*.53))),break_long_words=False) or [''])
    tf=slide.shapes.add_textbox(Pt(x),Pt(y),Pt(w),Pt(len(lines)*size*1.27+8)).text_frame
    tf.margin_left=tf.margin_right=tf.margin_top=tf.margin_bottom=0
    for i,line in enumerate(lines):
        p=tf.paragraphs[0] if i==0 else tf.add_paragraph()
        p.text=line; p.font.name='Arial'; p.font.size=Pt(size); p.font.bold=bold
        p.font.color.rgb=RGBColor.from_string(color); p.space_after=Pt(0); p.line_spacing=1.15
    pdf.setFillColor(HexColor('#'+color)); pdf.setFont(PDF_BOLD if bold else PDF_FONT,size)
    for i,line in enumerate(lines): pdf.drawString(x,540-y-size-i*size*1.15,line)

def start(n,tag,title,sub=''):
    global slide
    slide=prs.slides.add_slide(prs.slide_layouts[6])
    box(0,0,960,540,BG); box(40,38,35,4,ACCENT)
    txt(86,28,810,tag.upper(),12,ACCENT,True)
    txt(40,72,880,title,34,WHITE,True)
    if sub: txt(40,130,880,sub,18,MUTED)
    box(40,496,880,1,PANEL)
    txt(40,510,820,'PUC MINAS  /  PROJETO DE SOFTWARE  /  LABORATÓRIO 3',10,MUTED)
    txt(885,507,40,f'{n:02d}',14,ACCENT,True)

def end(notes):
    slide.notes_slide.notes_text_frame.text=notes
    pdf.showPage()

def cards(items,y=192,h=240):
    gap=16; w=(880-gap*(len(items)-1))/len(items)
    for i,(head,body) in enumerate(items):
        x=40+i*(w+gap); box(x,y,w,h,PANEL)
        txt(x+20,y+18,w-40,head,22,ACCENT,True)
        txt(x+20,y+70,w-40,body,19)

start(1,'Apresentação do projeto','Sistema de Moeda Estudantil','Reconhecimento acadêmico que se transforma em benefícios.')
txt(40,218,600,'Mérito → moedas → vantagens',32,ACCENT,True)
txt(40,302,850,'Guilherme Horta Dias\nLucas Batista Duarte\nRafael Abras',22)
txt(40,424,850,'Engenharia de Software · Profa. Milena Menezes Adão · 2026/2',17,MUTED)
end('Tempo: 30 s. Apresentar o grupo e o objetivo: reconhecer alunos por meio de moedas virtuais, com benefícios oferecidos por empresas parceiras.')

start(2,'Contexto','Do reconhecimento ao benefício','Três perfis participam de um único ciclo de mérito estudantil.')
cards([('Professor','Reconhece o aluno e registra o motivo do envio de moedas.'),('Aluno','Acompanha saldo e extrato e troca moedas por vantagens.'),('Empresa parceira','Publica ofertas e recebe o cupom do resgate por email.')])
end('Tempo: 40 s. Explicar o papel de cada perfil. As moedas são virtuais, usadas no contexto do laboratório. O professor não se cadastra publicamente: seus dados vêm da instituição.')

start(3,'Escopo implementado','Um fluxo completo de ponta a ponta','Da criação do cadastro ao cupom enviado por email.')
cards([('01 / Acesso','Cadastros de aluno e empresa.\nLogin e permissões por perfil.'),('02 / Mérito','Crédito semestral.\nEnvio com motivo.\nSaldo e extrato.'),('03 / Benefícios','Vantagens com foto.\nResgate com cupom.\nNotificações por email.')])
end('Tempo: 40 s. Apresentar a implementação atual em backend/ e frontend/. A versão inicial da Sprint 2 em SQLite foi preservada como entrega histórica; esta apresentação trata da aplicação integrada atual.')

start(4,'Regras de negócio','Como as moedas circulam','Exemplo ilustrativo de reconhecimento e resgate.')
cards([('1.000 moedas','O professor recebe crédito a cada semestre, somado ao saldo restante.'),('Reconhecimento','O professor envia 100 moedas e informa o motivo. O aluno recebe o crédito.'),('Resgate','Uma vantagem de 60 moedas gera um cupom. O aluno fica com 40 moedas.')],h=250)
end('Tempo: 50 s. Os valores 100 e 60 são exemplos, não dados reais. O aluno começa com zero; a empresa não possui carteira. O saldo não pode ficar negativo. O professor consulta alunos ativos da mesma instituição, decisão D09 do grupo.')

start(5,'Arquitetura MVC','Responsabilidades bem definidas','A interface apresenta; os serviços aplicam as regras de negócio.')
for i,(h,b) in enumerate([('React + TypeScript','Telas e chamadas HTTP'),('Controllers','HTTP, DTOs e validação'),('Services','Regras e transações'),('JPA + PostgreSQL','Entidades e persistência')]):
    x=40+i*224; box(x,210,208,150,PANEL); txt(x+14,230,180,h,21,ACCENT,True); txt(x+14,292,180,b,17)
    if i<3: txt(x+208,267,20,'›',19,ACCENT,True)
txt(40,400,870,'Spring Boot no backend · Flyway versiona o esquema do banco.',21,MUTED)
end('Tempo: 50 s. Explicar MVC e a separação em camadas. Controllers recebem DTOs; services verificam autorização e regras dentro de transações; repositories acessam PostgreSQL por Spring Data JPA. Hibernate valida o esquema criado pelo Flyway.')

start(6,'Modelo de dados','Identidade, saldo e histórico','Visão resumida das relações; o ER completo está na documentação.')
cards([('Identidade','Usuário vincula aluno, professor ou empresa.\nAluno e professor têm instituição.'),('Moedas','Aluno e professor têm conta.\nMovimentações registram créditos, envios e resgates.'),('Benefícios','Empresa publica vantagem.\nResgate guarda cupom.\nNotificação registra o email.')],h=264)
end('Tempo: 50 s. Referência: docs/modelagem/persistencia.md e diagramas/modelo-er.png. Aluno, professor e empresa associam o UUID de Usuario como PK e FK, sem herança JPA. O cupom fica na movimentação de resgate. A descrição e o custo resgatados são preservados no histórico.')

start(7,'Integridade','Uma operação, um registro consistente','Saldo, movimentação e intenção de email são confirmados juntos.')
cards([('Saldo protegido','Bloqueio de contas e validação impedem débito acima do saldo.'),('Sem duplicidade','Uma identificação por comando evita repetir o débito ao reenviar a operação.'),('Histórico preservado','Inativação conserva cadastros e movimentações já realizadas.')])
end('Tempo: 45 s. A unicidade de solicitante e requisicaoId protege o comando. Repetir o mesmo comando retorna o registro original; reutilizar o identificador com outro comando gera conflito. Crédito semestral também tem proteção contra duplicidade. Falhas causam rollback da operação.')

start(8,'Acesso e segurança','Cada perfil acessa suas funções','Autorização no backend acompanha a navegação da interface.')
cards([('Autenticação','Senhas com BCrypt.\nSessão com cookie HttpOnly e proteção CSRF.'),('Autorização','Aluno e empresa mantêm apenas seu próprio cadastro. Professor reconhece alunos.'),('Validação','DTOs e Bean Validation.\nEntidades e hashes de senha não são expostos pela API.')])
end('Tempo: 40 s. Explicar que esconder botões não basta: os serviços validam o titular e o perfil. Cadastro e login são públicos; demais operações exigem sessão. Professores são importados por arquivo institucional privado.')

start(9,'Emails e cupons','O resgate chega às duas partes','Aluno e empresa recebem o mesmo código de cupom.')
for i,(h,b) in enumerate([('Resgate confirmado','Banco registra débito, cupom e notificações.'),('Worker de email','Processa o envio e tenta novamente após falha.'),('Mailpit local','Captura mensagens em localhost:8025.')]):
    y=180+i*95; box(40,y,880,81,PANEL); txt(60,y+13,260,h,21,ACCENT,True); txt(350,y+14,540,b,20)
end('Tempo: 45 s. O envio SMTP ocorre após a confirmação da operação. Se falhar, apenas a notificação é repetida, sem novo débito. Mailpit captura emails locais; um provedor SMTP no .env permite entrega externa. A conferência do cupom é presencial, sem baixa digital nesta versão.')

start(10,'Demonstração ao vivo','Percorrer o ciclo na aplicação','Aplicação: localhost:5173  ·  Emails: localhost:8025')
steps=[('01','Aluno e empresa','Mostrar cadastros e publicar uma vantagem com foto.'),('02','Professor','Entrar, escolher aluno e enviar moedas com um motivo.'),('03','Aluno','Conferir o extrato e resgatar a vantagem.'),('04','Mailpit','Abrir os emails do resgate e comparar o código do cupom.')]
for i,(num,h,b) in enumerate(steps):
    y=181+i*72; txt(40,y,55,num,26,ACCENT,True); txt(105,y,245,h,21,WHITE,True); txt(360,y,550,b,19,MUTED)
end('Tempo: 2 a 3 min. Preparar previamente aluno, empresa e professor fictícios. Login do professor: professor.demo; consultar a senha no arquivo local, sem mostrá-la nos slides. A vantagem deve custar menos que o saldo disponível. Alternar as sessões ao demonstrar os perfis. Confirmar backend, frontend, PostgreSQL e Mailpit antes da apresentação. Os endereços são da máquina que executa a aplicação.')

start(11,'Validação e limites','O que foi verificado e o que falta','Separar implementação entregue de validação ainda pendente.')
cards([('Verificação registrada','A documentação registra compilação e uma verificação básica de integração.'),('Próxima etapa','Executar a campanha completa de testes e ampliar a validação dos fluxos.'),('Limites atuais','Mailpit captura emails locais.\nCupom tem conferência presencial.\nSem baixa digital.')],h=250)
end('Tempo: 35 s. Não afirmar que a suíte completa passou: a documentação indica que a campanha completa ficou para a etapa posterior. Estas verificações são as registradas no projeto, não uma nova execução feita para criar os slides. O SMTP externo depende da configuração de um provedor.')

start(12,'Encerramento','Do mérito ao benefício, com histórico','Cadastro → reconhecimento → resgate → cupom → email')
txt(40,220,865,'Três perfis conectados por um fluxo de moedas rastreável.',32,ACCENT,True)
txt(40,334,865,'MVC · PostgreSQL · transações · autorização · notificações',23)
txt(40,423,865,'Demonstração e perguntas',25,WHITE,True)
end('Tempo: 20 s. Retomar o objetivo do sistema e abrir para perguntas. Referências internas: README.md; docs/modelagem/persistencia.md; docs/modelagem/requisitos.md; código em backend/ e frontend/.')

prs.core_properties.title='Sistema de Moeda Estudantil'
prs.core_properties.author='Guilherme Horta Dias; Lucas Batista Duarte; Rafael Abras'
prs.core_properties.subject='PUC Minas — Projeto de Software — Laboratório 3'
prs.save(OUT / 'Sistema-de-Moeda-Estudantil.pptx')
pdf.save()
print(f'Gerados {len(prs.slides)} slides em {OUT}')
