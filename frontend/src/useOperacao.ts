import { useRef, useState } from 'react'
import { ApiError, json, mensagemErro, request } from './api'
import type { Movimentacao } from './api'

interface Tentativa<T> {
  requisicaoId: string
  dados: T
}

export default function useOperacao<T extends object>(chave: string, caminho: string) {
  const storageKey = `moeda-estudantil:operacao:v1:${chave}`
  const [pendente, setPendente] = useState<Tentativa<T> | null>(() => {
    try {
      const valor = JSON.parse(sessionStorage.getItem(storageKey) ?? 'null')
      return valor && typeof valor.requisicaoId === 'string' && valor.dados ? valor : null
    } catch {
      return null
    }
  })
  const [busy, setBusy] = useState(false)
  const [erro, setErro] = useState<string | null>(null)
  const enviando = useRef(false)

  async function executar(dados?: T): Promise<Movimentacao | null> {
    if (enviando.current || (!pendente && !dados)) return null
    const tentativa = pendente ?? { requisicaoId: crypto.randomUUID(), dados: dados! }
    enviando.current = true
    setBusy(true)
    setErro(null)
    setPendente(tentativa)
    try {
      // Persist before sending so a reload can retry the same operation safely.
      sessionStorage.setItem(storageKey, JSON.stringify(tentativa))
      const resultado = await request<Movimentacao>(
        caminho,
        json('POST', { ...tentativa.dados, requisicaoId: tentativa.requisicaoId }),
      )
      sessionStorage.removeItem(storageKey)
      setPendente(null)
      return resultado
    } catch (e) {
      const definitivo =
        e instanceof ApiError &&
        e.status >= 400 &&
        e.status < 500 &&
        ![401, 403, 408, 429].includes(e.status)
      if (definitivo) {
        sessionStorage.removeItem(storageKey)
        setPendente(null)
      }
      setErro(
        `${mensagemErro(e)}${definitivo ? '' : ' Tente confirmar novamente: a mesma identificação será usada para evitar duplicidade.'}`,
      )
      return null
    } finally {
      enviando.current = false
      setBusy(false)
    }
  }

  return { pendente, busy, erro, executar }
}
