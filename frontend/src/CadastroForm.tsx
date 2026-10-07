import { useState } from 'react'
import type { FormEvent } from 'react'
import type { Cadastro, DadosCadastro, Instituicao, NovoCadastro, Perfil } from './api'

interface Props {
  perfil: Perfil
  instituicoes: Instituicao[]
  inicial?: Cadastro
  busy: boolean
  onSave: (dados: DadosCadastro, novo?: NovoCadastro) => Promise<void>
  onCancel: () => void
}

export default function CadastroForm({
  perfil,
  instituicoes,
  inicial,
  busy,
  onSave,
  onCancel,
}: Props) {
  const aluno = perfil === 'ALUNO'
  const [dados, setDados] = useState<DadosCadastro>(() => ({
    nome: inicial?.nome ?? '',
    email: inicial?.email ?? '',
    cpf: inicial?.cpf ?? '',
    rg: inicial?.rg ?? '',
    endereco: inicial?.endereco ?? '',
    curso: inicial?.curso ?? '',
    instituicaoId: inicial?.instituicaoId,
  }))
  const [login, setLogin] = useState('')
  const [senha, setSenha] = useState('')
  function campo(nome: keyof DadosCadastro, valor: string) {
    setDados((d) => ({ ...d, [nome]: valor }))
  }
  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const payload: DadosCadastro = aluno ? dados : { nome: dados.nome, email: dados.email }
    await onSave(payload, inicial ? undefined : { login, senha, dados: payload })
  }
  return (
    <form className="cadastro-form" onSubmit={submit}>
      <fieldset disabled={busy}>
        <legend>Dados {aluno ? 'do aluno' : 'da empresa'}</legend>
        <div className="form-grid">
          <label className="wide">
            {aluno ? 'Nome completo' : 'Nome da empresa'}
            <input
              name="nome"
              autoComplete={aluno ? 'name' : 'organization'}
              value={dados.nome}
              onChange={(e) => campo('nome', e.target.value)}
              required
              maxLength={150}
            />
          </label>
          <label className="wide">
            Email
            <input
              name="email"
              type="email"
              autoComplete="email"
              value={dados.email}
              onChange={(e) => campo('email', e.target.value)}
              required
              maxLength={254}
            />
          </label>
          {aluno && (
            <>
              <label>
                <span id="cpf-label">CPF</span>{' '}
                <input
                  name="cpf"
                  inputMode="numeric"
                  value={dados.cpf}
                  onChange={(e) => campo('cpf', e.target.value)}
                  required
                  pattern="[0-9]{11}"
                  maxLength={11}
                  title="Informe 11 dígitos, sem pontuação."
                  aria-labelledby="cpf-label"
                  aria-describedby="cpf-hint"
                />
                <small id="cpf-hint">11 dígitos, sem pontuação</small>
              </label>
              <label>
                RG{' '}
                <input
                  name="rg"
                  value={dados.rg}
                  onChange={(e) => campo('rg', e.target.value)}
                  required
                  maxLength={30}
                />
              </label>
              <label className="wide">
                Endereço{' '}
                <input
                  name="endereco"
                  autoComplete="street-address"
                  value={dados.endereco}
                  onChange={(e) => campo('endereco', e.target.value)}
                  required
                  maxLength={300}
                />
              </label>
              <label>
                <span id="instituicao-label">Instituição</span>
                <select
                  name="instituicaoId"
                  aria-labelledby="instituicao-label"
                  required
                  value={dados.instituicaoId ?? ''}
                  onChange={(e) =>
                    setDados((d) => ({ ...d, instituicaoId: Number(e.target.value) }))
                  }
                >
                  <option value="" disabled>
                    Selecione
                  </option>
                  {instituicoes.map((i) => (
                    <option key={i.id} value={i.id}>
                      {i.nome}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                Curso{' '}
                <input
                  name="curso"
                  value={dados.curso}
                  onChange={(e) => campo('curso', e.target.value)}
                  required
                  maxLength={150}
                />
              </label>
            </>
          )}
        </div>
      </fieldset>
      {!inicial && (
        <fieldset disabled={busy}>
          <legend>Seu acesso</legend>
          <div className="form-grid">
            <label>
              Login{' '}
              <input
                name="login"
                autoComplete="username"
                value={login}
                onChange={(e) => setLogin(e.target.value)}
                required
                minLength={3}
                maxLength={80}
                pattern="[A-Za-z0-9._\-]{3,80}"
                title="De 3 a 80 letras, números, pontos, hífens ou sublinhados."
              />
            </label>
            <label>
              <span id="senha-label">Senha</span>{' '}
              <input
                name="senha"
                type="password"
                autoComplete="new-password"
                value={senha}
                onChange={(e) => setSenha(e.target.value)}
                required
                minLength={8}
                maxLength={72}
                aria-labelledby="senha-label"
                aria-describedby="senha-hint"
              />
              <small id="senha-hint">Pelo menos 8 caracteres</small>
            </label>
          </div>
        </fieldset>
      )}
      {inicial && (
        <p className="hint">
          Seu login é <strong>{inicial.login}</strong>. Ele permanece o mesmo após a edição.
        </p>
      )}
      <div className="form-actions">
        <button className="button primary" type="submit" disabled={busy}>
          {busy ? 'Salvando…' : inicial ? 'Salvar alterações' : 'Concluir cadastro'}
        </button>
        <button className="button text-button" type="button" disabled={busy} onClick={onCancel}>
          Cancelar
        </button>
      </div>
    </form>
  )
}
