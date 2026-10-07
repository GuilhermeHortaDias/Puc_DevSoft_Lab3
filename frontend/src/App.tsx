import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { ApiError, entrar, json, recurso, request } from './api'
import type { Cadastro, DadosCadastro, Instituicao, NovoCadastro, Perfil, Sessao } from './api'
import CadastroForm from './CadastroForm'
import './App.css'

export default function App() {
  const [sessao, setSessao] = useState<Sessao | null>(null)
  const [cadastro, setCadastro] = useState<Cadastro | null>(null)
  const [instituicoes, setInstituicoes] = useState<Instituicao[]>([])
  const [pagina, setPagina] = useState<Perfil | 'inicio'>('inicio')
  const [editando, setEditando] = useState(false)
  const [confirmando, setConfirmando] = useState(false)
  const [loading, setLoading] = useState(true)
  const [busy, setBusy] = useState(false)
  const [erro, setErro] = useState<string | null>(null)
  const [aviso, setAviso] = useState<string | null>(null)
  useEffect(() => {
    const abort = new AbortController()
    async function carregar() {
      try {
        const [usuario, lista] = await Promise.all([
          request<Sessao>('/auth/me', { signal: abort.signal }).catch((e) => {
            if (e instanceof ApiError && e.status === 401) return null
            throw e
          }),
          request<Instituicao[]>('/instituicoes', { signal: abort.signal }),
        ])
        const perfil = usuario
          ? await request<Cadastro>(`${recurso(usuario.perfil)}/${usuario.id}`, {
              signal: abort.signal,
            })
          : null
        if (!abort.signal.aborted) {
          setSessao(usuario)
          setCadastro(perfil)
          setInstituicoes(lista)
        }
      } catch (e) {
        if (!abort.signal.aborted) mostrarErro(e)
      } finally {
        if (!abort.signal.aborted) setLoading(false)
      }
    }
    void carregar()
    return () => abort.abort()
  }, [])
  function mostrarErro(e: unknown) {
    if (e instanceof ApiError) {
      const campos = Object.entries(e.campos)
        .map(([campo, mensagem]) => `${campo.replace('dados.', '')}: ${mensagem}`)
        .join(' · ')
      setErro(campos ? `${e.message} ${campos}` : e.message)
    } else {
      setErro('Não foi possível conectar ao sistema. Confira sua conexão e tente novamente.')
    }
  }
  function navegar(destino: Perfil | 'inicio') {
    setPagina(destino)
    setErro(null)
    setAviso(null)
  }
  async function iniciarSessao(login: string, senha: string) {
    const usuario = await entrar(login, senha)
    const registro = await request<Cadastro>(`${recurso(usuario.perfil)}/${usuario.id}`)
    setSessao(usuario)
    setCadastro(registro)
    setPagina('inicio')
    setEditando(false)
    setConfirmando(false)
  }
  async function loginSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setBusy(true)
    setErro(null)
    setAviso(null)
    const form = new FormData(event.currentTarget)
    try {
      await iniciarSessao(String(form.get('login')), String(form.get('senha')))
    } catch (e) {
      mostrarErro(e)
    } finally {
      setBusy(false)
    }
  }
  async function salvar(dados: DadosCadastro, novo?: NovoCadastro) {
    setBusy(true)
    setErro(null)
    setAviso(null)
    let criado = false
    try {
      if (sessao) {
        const registro = await request<Cadastro>(
          `${recurso(sessao.perfil)}/${sessao.id}`,
          json('PUT', dados),
        )
        setCadastro(registro)
        setSessao((s) => (s ? { ...s, nome: registro.nome } : s))
        setEditando(false)
        setAviso('Cadastro atualizado com sucesso.')
      } else if (novo && pagina !== 'inicio') {
        await request<Cadastro>(recurso(pagina), json('POST', novo))
        criado = true
        await iniciarSessao(novo.login, novo.senha)
        setAviso('Cadastro concluído. Bem-vindo!')
      }
    } catch (e) {
      if (criado) {
        setPagina('inicio')
        setAviso('Seu cadastro foi criado. Entre com seu login e senha.')
        setErro(null)
      } else {
        mostrarErro(e)
      }
    } finally {
      setBusy(false)
    }
  }
  async function encerrar(inativar: boolean) {
    if (!sessao) return
    setBusy(true)
    setErro(null)
    try {
      if (inativar)
        await request<void>(`${recurso(sessao.perfil)}/${sessao.id}`, { method: 'DELETE' })
      else await request<void>('/auth/logout', { method: 'POST' })
      setSessao(null)
      setCadastro(null)
      setPagina('inicio')
      setEditando(false)
      setConfirmando(false)
      setAviso(inativar ? 'Cadastro inativado com sucesso.' : null)
    } catch (e) {
      mostrarErro(e)
    } finally {
      setBusy(false)
    }
  }
  return (
    <>
      <a className="skip-link" href="#conteudo">
        Ir para o conteúdo
      </a>
      <header className="site-header">
        <button
          className="brand"
          onClick={() => {
            if (!sessao) navegar('inicio')
          }}
          aria-label="Moeda Estudantil — início"
        >
          <span className="brand-mark" aria-hidden="true">
            m<span>e</span>
          </span>
          <span>
            Moeda Estudantil<small>PUC MINAS · PROJETO DE SOFTWARE</small>
          </span>
        </button>
        <div className="header-right">
          <span className="edition">Reconhecer. Conectar. Transformar.</span>
          {sessao && (
            <button
              className="button secondary small-button"
              disabled={busy}
              onClick={() => void encerrar(false)}
            >
              Sair
            </button>
          )}
        </div>
      </header>
      <main id="conteudo" className="container">
        {erro && (
          <div className="notice error" role="alert">
            {erro}
          </div>
        )}
        {aviso && (
          <div className="notice success" role="status">
            {aviso}
          </div>
        )}
        {loading ? (
          <p className="loading" role="status">
            Conectando ao sistema…
          </p>
        ) : sessao && cadastro ? (
          <>
            <div className="page-heading">
              <div>
                <p className="eyebrow">SEU ESPAÇO</p>
                <h1>Meu cadastro</h1>
                <p>Mantenha suas informações atualizadas para participar.</p>
              </div>
              <span className="profile-badge">
                {sessao.perfil === 'ALUNO' ? 'Aluno' : 'Empresa parceira'}
              </span>
            </div>
            <div className="profile-layout">
              <section className="panel profile-panel">
                {editando ? (
                  <>
                    <h2>Atualizar cadastro</h2>
                    <CadastroForm
                      perfil={sessao.perfil}
                      instituicoes={instituicoes}
                      inicial={cadastro}
                      busy={busy}
                      onSave={salvar}
                      onCancel={() => {
                        setEditando(false)
                        setErro(null)
                      }}
                    />
                  </>
                ) : (
                  <>
                    <div className="section-heading">
                      <h2>Suas informações</h2>
                      <button
                        className="button secondary"
                        onClick={() => {
                          setEditando(true)
                          setConfirmando(false)
                          setAviso(null)
                        }}
                      >
                        Editar cadastro
                      </button>
                    </div>
                    <dl className="details-grid">
                      <div className="wide">
                        <dt>{sessao.perfil === 'ALUNO' ? 'Nome completo' : 'Nome da empresa'}</dt>
                        <dd>{cadastro.nome}</dd>
                      </div>
                      <div>
                        <dt>Email</dt>
                        <dd>{cadastro.email}</dd>
                      </div>
                      <div>
                        <dt>Login</dt>
                        <dd>{cadastro.login}</dd>
                      </div>
                      {sessao.perfil === 'ALUNO' && (
                        <>
                          <div>
                            <dt>CPF</dt>
                            <dd>{cadastro.cpf}</dd>
                          </div>
                          <div>
                            <dt>RG</dt>
                            <dd>{cadastro.rg}</dd>
                          </div>
                          <div className="wide">
                            <dt>Endereço</dt>
                            <dd>{cadastro.endereco}</dd>
                          </div>
                          <div>
                            <dt>Instituição</dt>
                            <dd>{cadastro.instituicao}</dd>
                          </div>
                          <div>
                            <dt>Curso</dt>
                            <dd>{cadastro.curso}</dd>
                          </div>
                        </>
                      )}
                    </dl>
                  </>
                )}
              </section>
              <aside className="profile-aside">
                <section className="balance-card">
                  <p className="eyebrow">
                    {sessao.perfil === 'ALUNO' ? 'SUA CONTA' : 'SUA PARCERIA'}
                  </p>
                  {sessao.perfil === 'ALUNO' ? (
                    <>
                      <strong className="balance">
                        {cadastro.saldo}
                        <span>moedas</span>
                      </strong>
                      <p>Seu cadastro está ativo. Você começa com saldo zero.</p>
                    </>
                  ) : (
                    <>
                      <h2>Conexões que fazem a diferença.</h2>
                      <p>Sua empresa agora faz parte do sistema de mérito estudantil.</p>
                    </>
                  )}
                </section>
                <section className="danger-card">
                  <h2>Gerenciar participação</h2>
                  <p>
                    Ao inativar, seu acesso será encerrado. Seus dados serão conservados para o
                    histórico.
                  </p>
                  {confirmando ? (
                    <>
                      <p className="confirmation">Deseja inativar seu cadastro?</p>
                      <button
                        className="button danger"
                        disabled={busy}
                        onClick={() => void encerrar(true)}
                      >
                        Confirmar inativação
                      </button>
                      <button
                        className="button text-button"
                        disabled={busy}
                        onClick={() => setConfirmando(false)}
                      >
                        Cancelar
                      </button>
                    </>
                  ) : (
                    <button
                      className="danger-link"
                      disabled={busy}
                      onClick={() => setConfirmando(true)}
                    >
                      Inativar cadastro
                    </button>
                  )}
                </section>
              </aside>
            </div>
          </>
        ) : pagina !== 'inicio' ? (
          <>
            <button className="back-button" onClick={() => navegar('inicio')}>
              ← Voltar ao início
            </button>
            <div className="registration-layout">
              <div className="registration-intro">
                <p className="eyebrow">FAÇA PARTE</p>
                <h1>
                  {pagina === 'ALUNO' ? 'Crie sua conta de aluno.' : 'Seja uma empresa parceira.'}
                </h1>
                <p>
                  {pagina === 'ALUNO'
                    ? 'O primeiro passo para transformar seu mérito em novas oportunidades.'
                    : 'Conecte sua empresa a uma comunidade que valoriza dedicação e conhecimento.'}
                </p>
                <div className="intro-note">
                  <span aria-hidden="true">↗</span>
                  <p>
                    {pagina === 'ALUNO'
                      ? 'Escolha sua instituição e informe seus dados para começar.'
                      : 'Informe o nome e o email da empresa. Suas credenciais dão acesso ao próprio cadastro.'}
                  </p>
                </div>
              </div>
              <section className="panel registration-panel">
                <h2>{pagina === 'ALUNO' ? 'Cadastro de aluno' : 'Cadastro de empresa'}</h2>
                <p className="hint">Todos os campos são obrigatórios.</p>
                <CadastroForm
                  key={pagina}
                  perfil={pagina}
                  instituicoes={instituicoes}
                  busy={busy}
                  onSave={salvar}
                  onCancel={() => navegar('inicio')}
                />
              </section>
            </div>
          </>
        ) : (
          <>
            <div className="home-layout">
              <section className="hero">
                <p className="eyebrow">
                  <span className="status-dot" /> MÉRITO ESTUDANTIL
                </p>
                <h1>
                  Seu esforço merece
                  <br />
                  <em>novas possibilidades.</em>
                </h1>
                <p className="hero-description">
                  Um espaço para reconhecer a dedicação dos alunos e aproximar a comunidade
                  acadêmica de empresas parceiras.
                </p>
                <div className="hero-actions">
                  <button className="button primary" onClick={() => navegar('ALUNO')}>
                    Criar conta de aluno <span aria-hidden="true">↗</span>
                  </button>
                  <button className="button secondary" onClick={() => navegar('EMPRESA')}>
                    Cadastrar empresa
                  </button>
                </div>
                <div className="merit-visual" aria-hidden="true">
                  <div className="coin">
                    m<span>e</span>
                  </div>
                  <div className="visual-copy">
                    <strong>Dedicação tem valor.</strong>
                    <span>Uma comunidade. Muitas oportunidades.</span>
                  </div>
                  <span className="visual-spark">✳</span>
                </div>
              </section>
              <section className="panel login-panel">
                <span className="login-icon" aria-hidden="true">
                  ↗
                </span>
                <h2>Acesse sua conta</h2>
                <p>Que bom ter você por aqui.</p>
                <form onSubmit={loginSubmit}>
                  <fieldset disabled={busy}>
                    <legend className="sr-only">Credenciais de acesso</legend>
                    <label>
                      Login{' '}
                      <input
                        name="login"
                        autoComplete="username"
                        required
                        maxLength={80}
                        placeholder="Seu login"
                      />
                    </label>
                    <label>
                      Senha{' '}
                      <input
                        name="senha"
                        type="password"
                        autoComplete="current-password"
                        required
                        maxLength={72}
                        placeholder="Sua senha"
                      />
                    </label>
                    <button type="submit" className="button primary" disabled={busy}>
                      {busy ? 'Entrando…' : 'Entrar'}
                    </button>
                  </fieldset>
                </form>
                <p className="login-note">
                  Aluno ou empresa parceira?
                  <br />
                  Use o acesso criado no seu cadastro.
                </p>
              </section>
            </div>
            <section className="how-it-works" aria-labelledby="como-funciona">
              <div className="section-heading">
                <h2 id="como-funciona">Uma ideia, mais conexões.</h2>
                <span>O propósito do sistema</span>
              </div>
              <div className="steps">
                <article>
                  <span className="step-number">01</span>
                  <h3>Reconhecer o mérito</h3>
                  <p>Professores valorizam a dedicação dos alunos com moedas virtuais.</p>
                </article>
                <article>
                  <span className="step-number">02</span>
                  <h3>Aproximar parceiros</h3>
                  <p>Empresas oferecem benefícios à comunidade acadêmica.</p>
                </article>
                <article>
                  <span className="step-number">03</span>
                  <h3>Abrir possibilidades</h3>
                  <p>Alunos trocam suas moedas por produtos e descontos.</p>
                </article>
              </div>
              <p className="scope-note">
                Nesta versão, estão disponíveis o cadastro e a manutenção de dados. As funções de
                moedas e benefícios serão adicionadas nas próximas etapas.
              </p>
            </section>
          </>
        )}
      </main>
      <footer className="site-footer">
        <span>Moeda Estudantil</span>
        <p>Projeto acadêmico · PUC Minas · 2026</p>
      </footer>
    </>
  )
}
