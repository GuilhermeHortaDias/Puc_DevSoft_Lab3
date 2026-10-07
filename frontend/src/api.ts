export type Perfil = 'ALUNO' | 'EMPRESA'
export interface Sessao {
  id: string
  login: string
  perfil: Perfil
  nome: string
}
export interface Instituicao {
  id: number
  nome: string
}
export interface DadosCadastro {
  nome: string
  email: string
  cpf?: string
  rg?: string
  endereco?: string
  curso?: string
  instituicaoId?: number
}
export interface Cadastro extends DadosCadastro {
  id: string
  login: string
  instituicao?: string
  saldo?: number
}
export interface NovoCadastro {
  login: string
  senha: string
  dados: DadosCadastro
}

export class ApiError extends Error {
  status: number
  campos: Record<string, string>
  constructor(status: number, mensagem: string, campos: Record<string, string> = {}) {
    super(mensagem)
    this.status = status
    this.campos = campos
  }
}

export async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const headers = new Headers(options.headers)
  if (options.method && !['GET', 'HEAD'].includes(options.method)) {
    const tokenResponse = await fetch('/api/auth/csrf', { credentials: 'same-origin' })
    if (!tokenResponse.ok)
      throw new ApiError(
        tokenResponse.status,
        'Não foi possível iniciar uma operação segura. Atualize a página.',
      )
    const csrf: { token: string; headerName: string } = await tokenResponse.json()
    headers.set(csrf.headerName, csrf.token)
  }
  const response = await fetch(`/api${path}`, { ...options, headers, credentials: 'same-origin' })
  const text = await response.text()
  const body =
    text && response.headers.get('content-type')?.includes('json') ? JSON.parse(text) : undefined
  if (!response.ok)
    throw new ApiError(
      response.status,
      body?.mensagem ?? 'Não foi possível concluir a operação. Tente novamente.',
      body?.campos,
    )
  return body as T
}
export function json(method: string, body: unknown): RequestInit {
  return { method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body) }
}
export async function entrar(login: string, senha: string): Promise<Sessao> {
  await request<void>('/auth/login', {
    method: 'POST',
    body: new URLSearchParams({ username: login, password: senha }),
  })
  return request<Sessao>('/auth/me')
}
export function recurso(perfil: Perfil): string {
  return perfil === 'ALUNO' ? '/alunos' : '/empresas'
}
