import { useEffect, useState } from 'react'
import type { ReactNode } from 'react'
import { mensagemErro, request } from './api'
import type { Professor, Sessao } from './api'
import ContaArea from './ContaArea'
import VantagensArea from './VantagensArea'

function PerfilProfessor() {
  const [professor, setProfessor] = useState<Professor | null>(null)
  const [erro, setErro] = useState<string | null>(null)
  const [versao, setVersao] = useState(0)
  useEffect(() => {
    const abort = new AbortController()
    request<Professor>('/professores/me', { signal: abort.signal })
      .then((dados) => {
        if (!abort.signal.aborted) {
          setProfessor(dados)
          setErro(null)
        }
      })
      .catch((e) => {
        if (!abort.signal.aborted) setErro(mensagemErro(e))
      })
    return () => abort.abort()
  }, [versao])
  return (
    <section className="panel">
      <h2>Meus dados de professor</h2>
      <p className="hint">Seu cadastro é preparado pela instituição.</p>
      {erro && (
        <div className="notice error" role="alert">
          {erro}
          <button className="button secondary small-button" onClick={() => setVersao((v) => v + 1)}>
            Tentar novamente
          </button>
        </div>
      )}
      {professor ? (
        <dl className="details-grid">
          <div>
            <dt>Nome</dt>
            <dd>{professor.nome}</dd>
          </div>
          <div>
            <dt>Login</dt>
            <dd>{professor.login}</dd>
          </div>
          <div>
            <dt>CPF</dt>
            <dd>{professor.cpf}</dd>
          </div>
          <div>
            <dt>Departamento</dt>
            <dd>{professor.departamento}</dd>
          </div>
          <div className="wide">
            <dt>Instituição</dt>
            <dd>{professor.instituicao}</dd>
          </div>
        </dl>
      ) : (
        !erro && <p role="status">Carregando seus dados…</p>
      )}
    </section>
  )
}

export default function Portal({ sessao, children }: { sessao: Sessao; children: ReactNode }) {
  const [pagina, setPagina] = useState<'conta' | 'vantagens' | 'cadastro'>(
    sessao.perfil === 'EMPRESA' ? 'vantagens' : 'conta',
  )
  const [versaoConta, setVersaoConta] = useState(0)
  const atualizarConta = () => setVersaoConta((v) => v + 1)
  return (
    <>
      <div className="page-heading">
        <div>
          <p className="eyebrow">SEU ESPAÇO</p>
          <h1>Olá, {sessao.nome}.</h1>
          <p>
            {sessao.perfil === 'PROFESSOR'
              ? 'Reconheça a dedicação e acompanhe suas moedas.'
              : sessao.perfil === 'EMPRESA'
                ? 'Conecte suas vantagens à comunidade acadêmica.'
                : 'Acompanhe suas conquistas e descubra novas possibilidades.'}
          </p>
        </div>
        <span className="profile-badge">
          {{ ALUNO: 'Aluno', EMPRESA: 'Empresa parceira', PROFESSOR: 'Professor' }[sessao.perfil]}
        </span>
      </div>
      <nav className="portal-nav" aria-label="Área da conta">
        {sessao.perfil !== 'EMPRESA' && (
          <button
            className={pagina === 'conta' ? 'active' : ''}
            aria-current={pagina === 'conta' ? 'page' : undefined}
            onClick={() => setPagina('conta')}
          >
            Saldo e extrato
          </button>
        )}
        {sessao.perfil !== 'PROFESSOR' && (
          <button
            className={pagina === 'vantagens' ? 'active' : ''}
            aria-current={pagina === 'vantagens' ? 'page' : undefined}
            onClick={() => setPagina('vantagens')}
          >
            {sessao.perfil === 'EMPRESA' ? 'Minhas vantagens' : 'Catálogo de vantagens'}
          </button>
        )}
        <button
          className={pagina === 'cadastro' ? 'active' : ''}
          aria-current={pagina === 'cadastro' ? 'page' : undefined}
          onClick={() => setPagina('cadastro')}
        >
          Meu cadastro
        </button>
      </nav>
      <div hidden={pagina !== 'conta'}>
        {sessao.perfil !== 'EMPRESA' && (
          <ContaArea sessao={sessao} versao={versaoConta} onRefresh={atualizarConta} />
        )}
      </div>
      <div hidden={pagina !== 'vantagens'}>
        {sessao.perfil !== 'PROFESSOR' && (
          <VantagensArea sessao={sessao} onResgate={atualizarConta} />
        )}
      </div>
      <div hidden={pagina !== 'cadastro'}>
        {sessao.perfil === 'PROFESSOR' ? <PerfilProfessor /> : children}
      </div>
    </>
  )
}
