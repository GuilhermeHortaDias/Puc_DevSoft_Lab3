import { useEffect, useRef, useState } from 'react'
import type { FormEvent } from 'react'
import { mensagemErro, request } from './api'
import type { Movimentacao, Sessao, Vantagem } from './api'
import useOperacao from './useOperacao'

const numeros = new Intl.NumberFormat('pt-BR')

function CadastroVantagem({ onSuccess }: { onSuccess: () => void }) {
  const [busy, setBusy] = useState(false)
  const [erro, setErro] = useState<string | null>(null)
  const [sucesso, setSucesso] = useState(false)
  const enviando = useRef(false)

  async function salvar(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (enviando.current) return
    const formulario = event.currentTarget
    const dados = new FormData(formulario)
    const foto = dados.get('foto')
    const custo = Number(dados.get('custo'))
    const descricao = String(dados.get('descricao')).trim()
    setErro(null)
    setSucesso(false)
    if (!descricao || !Number.isSafeInteger(custo) || custo <= 0) {
      setErro('Informe a descrição e um custo inteiro positivo em moedas.')
      return
    }
    if (
      !(foto instanceof File) ||
      !['image/jpeg', 'image/png'].includes(foto.type) ||
      foto.size === 0 ||
      foto.size > 2 * 1024 * 1024
    ) {
      setErro('Selecione uma foto PNG ou JPEG de até 2 MB.')
      return
    }
    dados.set('descricao', descricao)
    enviando.current = true
    setBusy(true)
    try {
      await request<Vantagem>('/vantagens', { method: 'POST', body: dados })
      formulario.reset()
      setSucesso(true)
      onSuccess()
    } catch (e) {
      setErro(mensagemErro(e))
    } finally {
      enviando.current = false
      setBusy(false)
    }
  }

  return (
    <section className="panel" aria-labelledby="cadastro-vantagem-titulo">
      <h2 id="cadastro-vantagem-titulo">Cadastrar vantagem</h2>
      <p className="hint">Apresente seu benefício aos alunos. Todos os campos são obrigatórios.</p>
      {erro && (
        <p className="notice error" role="alert">
          {erro}
        </p>
      )}
      {sucesso && (
        <p className="notice success" role="status">
          Vantagem cadastrada e disponível no catálogo.
        </p>
      )}
      <form onSubmit={salvar}>
        <fieldset disabled={busy}>
          <legend className="sr-only">Dados da vantagem</legend>
          <div className="form-grid">
            <label className="wide">
              Descrição
              <textarea name="descricao" rows={3} maxLength={2000} required />
            </label>
            <label>
              Custo em moedas
              <input
                name="custo"
                type="number"
                min="1"
                max={Number.MAX_SAFE_INTEGER}
                step="1"
                inputMode="numeric"
                required
              />
            </label>
            <label>
              Foto da vantagem
              <input
                name="foto"
                type="file"
                accept="image/png,image/jpeg"
                required
                aria-describedby="foto-hint"
              />
              <small id="foto-hint">PNG ou JPEG, até 2 MB.</small>
            </label>
          </div>
          <div className="form-actions">
            <button className="button primary" type="submit">
              {busy ? 'Cadastrando…' : 'Cadastrar vantagem'}
            </button>
          </div>
        </fieldset>
      </form>
    </section>
  )
}

export default function VantagensArea({
  sessao,
  onResgate,
}: {
  sessao: Sessao
  onResgate: () => void
}) {
  const [vantagens, setVantagens] = useState<Vantagem[]>([])
  const [erro, setErro] = useState<string | null>(null)
  const [versao, setVersao] = useState(0)
  const [versaoCarregada, setVersaoCarregada] = useState(-1)
  const carregando = versaoCarregada !== versao
  const [selecionada, setSelecionada] = useState<Vantagem | null>(null)
  const [cupom, setCupom] = useState<Movimentacao | null>(null)
  const confirmacao = useRef<HTMLHeadingElement>(null)
  const operacao = useOperacao<{ vantagemId: string }>(`resgate:${sessao.id}`, '/moedas/resgates')
  const empresa = sessao.perfil === 'EMPRESA'
  const vantagemPendente = vantagens.find((v) => v.id === operacao.pendente?.dados.vantagemId)
  const escolha = operacao.pendente ? vantagemPendente : selecionada
  const confirmarVisivel = !empresa && (!!operacao.pendente || !!selecionada)

  useEffect(() => {
    const abort = new AbortController()
    request<Vantagem[]>('/vantagens', { signal: abort.signal })
      .then((lista) => {
        if (!abort.signal.aborted) {
          setVantagens(lista)
          setErro(null)
        }
      })
      .catch((e) => {
        if (!abort.signal.aborted) setErro(mensagemErro(e))
      })
      .finally(() => {
        if (!abort.signal.aborted) setVersaoCarregada(versao)
      })
    return () => abort.abort()
  }, [versao])
  useEffect(() => {
    if (confirmarVisivel) confirmacao.current?.focus()
  }, [confirmarVisivel, selecionada?.id])

  async function resgatar() {
    const id = operacao.pendente?.dados.vantagemId ?? selecionada?.id
    if (!id) return
    setCupom(null)
    const resultado = await operacao.executar({ vantagemId: id })
    if (resultado) {
      setCupom(resultado)
      setSelecionada(null)
      onResgate()
    }
  }

  return (
    <div className="activity-stack">
      {empresa && <CadastroVantagem onSuccess={() => setVersao((v) => v + 1)} />}
      <div className="section-heading">
        <h2>{empresa ? 'Minhas vantagens' : 'Vantagens para você'}</h2>
        <button
          className="button secondary small-button"
          disabled={carregando}
          onClick={() => setVersao((v) => v + 1)}
        >
          Atualizar vantagens
        </button>
      </div>
      {erro && (
        <p className="notice error" role="alert">
          {erro}
        </p>
      )}
      {operacao.erro && (
        <p className="notice error" role="alert">
          {operacao.erro}
        </p>
      )}
      {cupom && (
        <section className="notice success coupon-result" role="status">
          <h2>Resgate confirmado</h2>
          <p>
            {numeros.format(cupom.quantidade)} moedas debitadas. Apresente este código na empresa
            para a troca presencial:
          </p>
          <strong className="coupon-code">{cupom.cupomCodigo}</strong>
          <p>
            Os emails para você e para a empresa serão processados após a confirmação. O código
            também está disponível no seu extrato.
          </p>
        </section>
      )}
      {confirmarVisivel && (
        <section className="panel redemption-panel" aria-labelledby="confirmar-resgate-titulo">
          <h2 id="confirmar-resgate-titulo" tabIndex={-1} ref={confirmacao}>
            {operacao.pendente ? 'Confirmar resgate pendente' : 'Confirmar resgate'}
          </h2>
          {escolha ? (
            <>
              <p>{escolha.descricao}</p>
              <p>
                {escolha.empresa} · <strong>{numeros.format(escolha.custo)} moedas</strong>
              </p>
            </>
          ) : (
            <p>
              Vantagem selecionada anteriormente. Confirme novamente para consultar o resultado da
              operação.
            </p>
          )}
          <p className="hint">
            O custo será descontado do seu saldo. Você receberá um código para apresentar na
            empresa.
          </p>
          {operacao.pendente && (
            <p className="hint">A mesma identificação será usada para evitar um segundo débito.</p>
          )}
          <div className="form-actions">
            <button
              className="button primary"
              disabled={operacao.busy}
              onClick={() => void resgatar()}
            >
              {operacao.busy
                ? 'Confirmando…'
                : operacao.pendente
                  ? 'Confirmar novamente'
                  : 'Confirmar troca de moedas'}
            </button>
            {!operacao.pendente && (
              <button
                className="button text-button"
                disabled={operacao.busy}
                onClick={() => setSelecionada(null)}
              >
                Cancelar
              </button>
            )}
          </div>
        </section>
      )}
      {carregando ? (
        <p className="hint" role="status">
          Carregando vantagens…
        </p>
      ) : vantagens.length === 0 && !erro ? (
        <p className="empty-state">
          {empresa
            ? 'Sua empresa ainda não cadastrou vantagens.'
            : 'Ainda não há vantagens disponíveis. Volte em breve.'}
        </p>
      ) : null}
      <div className="advantages-grid">
        {vantagens.map((vantagem) => (
          <article className="advantage-card" key={vantagem.id}>
            <img src={vantagem.fotoUrl} alt={`Foto: ${vantagem.descricao}`} loading="lazy" />
            <div className="advantage-body">
              <p className="advantage-company">{vantagem.empresa}</p>
              <h3>{vantagem.descricao}</h3>
              <strong className="advantage-price">
                {numeros.format(vantagem.custo)} <span>moedas</span>
              </strong>
              {!empresa && (
                <button
                  className="button secondary"
                  disabled={operacao.busy || !!operacao.pendente}
                  onClick={() => {
                    setSelecionada(vantagem)
                    setCupom(null)
                  }}
                >
                  Selecionar vantagem<span className="sr-only">: {vantagem.descricao}</span>
                </button>
              )}
            </div>
          </article>
        ))}
      </div>
    </div>
  )
}
