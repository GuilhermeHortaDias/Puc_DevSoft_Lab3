import { useEffect, useState } from 'react'
import { mensagemErro, request } from './api'
import type { AlunoDestinatario, Conta, Movimentacao, Sessao } from './api'
import type { FormEvent } from 'react'
import useOperacao from './useOperacao'

const numeros = new Intl.NumberFormat('pt-BR')
const datas = new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short', timeStyle: 'short' })

export function Saldo({ saldo }: { saldo: number }) {
  return (
    <section className="balance-card" aria-label="Saldo disponível">
      <p className="eyebrow">SEU SALDO</p>
      <strong className="balance">
        {numeros.format(saldo)}
        <span>moedas</span>
      </strong>
      <p>Saldo disponível para suas próximas movimentações.</p>
    </section>
  )
}

export function Extrato({ itens }: { itens: Movimentacao[] }) {
  return (
    <section className="panel extrato-panel" aria-labelledby="extrato-titulo">
      <h2 id="extrato-titulo">Extrato de moedas</h2>
      {itens.length === 0 ? (
        <p className="empty-state">Nenhuma movimentação por enquanto.</p>
      ) : (
        <ol className="transaction-list">
          {itens.map((item) => (
            <li key={item.id}>
              <div className="transaction-heading">
                <h3>
                  {item.descricao ||
                    {
                      CREDITO_SEMESTRAL: 'Crédito semestral',
                      RECONHECIMENTO: 'Reconhecimento',
                      RESGATE: 'Resgate de vantagem',
                    }[item.tipo]}
                </h3>
                <strong className={item.direcao === 'ENTRADA' ? 'amount-in' : 'amount-out'}>
                  <span className="sr-only">
                    {item.direcao === 'ENTRADA' ? 'Entrada de ' : 'Saída de '}
                  </span>
                  <span aria-hidden="true">{item.direcao === 'ENTRADA' ? '+' : '−'}</span>
                  {numeros.format(item.quantidade)} moedas
                </strong>
              </div>
              <time dateTime={item.dataHora}>{datas.format(new Date(item.dataHora))}</time>
              {item.contraparte && <p>{item.contraparte}</p>}
              {item.motivo && <p className="transaction-reason">{item.motivo}</p>}
              {item.cupomCodigo && (
                <p>
                  Cupom: <code className="coupon-code">{item.cupomCodigo}</code> · Apresente na
                  empresa para a troca presencial.
                </p>
              )}
            </li>
          ))}
        </ol>
      )}
    </section>
  )
}

interface Envio {
  alunoId: string
  quantidade: number
  motivo: string
}

function EnviarMoedas({
  sessao,
  saldo,
  onSuccess,
}: {
  sessao: Sessao
  saldo: number
  onSuccess: () => void
}) {
  const [alunos, setAlunos] = useState<AlunoDestinatario[] | null>(null)
  const [erroAlunos, setErroAlunos] = useState<string | null>(null)
  const [versao, setVersao] = useState(0)
  const [sucesso, setSucesso] = useState<string | null>(null)
  const [validacao, setValidacao] = useState<string | null>(null)
  const operacao = useOperacao<Envio>(`envio:${sessao.id}`, '/moedas/envios')
  useEffect(() => {
    const abort = new AbortController()
    request<AlunoDestinatario[]>('/professores/alunos', { signal: abort.signal })
      .then((lista) => {
        if (!abort.signal.aborted) {
          setAlunos(lista)
          setErroAlunos(null)
        }
      })
      .catch((e) => {
        if (!abort.signal.aborted) setErroAlunos(mensagemErro(e))
      })
    return () => abort.abort()
  }, [versao])

  async function enviar(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const formulario = event.currentTarget
    const form = new FormData(formulario)
    const dados = operacao.pendente?.dados ?? {
      alunoId: String(form.get('alunoId')),
      quantidade: Number(form.get('quantidade')),
      motivo: String(form.get('motivo')).trim(),
    }
    setSucesso(null)
    setValidacao(null)
    if (!Number.isSafeInteger(dados.quantidade) || dados.quantidade <= 0 || !dados.motivo) {
      setValidacao('Informe uma quantidade inteira positiva e o motivo do reconhecimento.')
      return
    }
    const resultado = await operacao.executar(dados)
    if (resultado) {
      formulario.reset()
      setSucesso(
        `Envio de ${numeros.format(resultado.quantidade)} moedas confirmado. O email ao aluno será processado após a confirmação.`,
      )
      onSuccess()
    }
  }

  return (
    <section className="panel" aria-labelledby="enviar-titulo">
      <h2 id="enviar-titulo">Reconhecer um aluno</h2>
      <p className="hint">
        Alunos ativos da sua instituição. Conte o que motivou esse reconhecimento.
      </p>
      {sucesso && (
        <p className="notice success" role="status">
          {sucesso}
        </p>
      )}
      {(operacao.erro || validacao) && (
        <p className="notice error" role="alert">
          {validacao ?? operacao.erro}
        </p>
      )}
      {erroAlunos && (
        <div className="notice error" role="alert">
          {erroAlunos}
          <button className="button secondary small-button" onClick={() => setVersao((v) => v + 1)}>
            Recarregar alunos
          </button>
        </div>
      )}
      {operacao.pendente && (
        <p className="notice pending" role="status">
          Confirmação pendente: {numeros.format(operacao.pendente.dados.quantidade)} moedas. Motivo:{' '}
          {operacao.pendente.dados.motivo}. Confirme novamente para consultar o resultado com
          segurança.
        </p>
      )}
      <form onSubmit={enviar}>
        <fieldset disabled={operacao.busy || !!operacao.pendente || !alunos?.length}>
          <legend className="sr-only">Dados do reconhecimento</legend>
          <div className="form-grid">
            <label className="wide">
              Aluno
              <select name="alunoId" required defaultValue="">
                <option value="" disabled>
                  {alunos === null
                    ? 'Carregando alunos…'
                    : alunos.length
                      ? 'Selecione um aluno'
                      : 'Nenhum aluno ativo disponível'}
                </option>
                {alunos?.map((aluno) => (
                  <option key={aluno.id} value={aluno.id}>
                    {aluno.nome} · {aluno.curso}
                  </option>
                ))}
              </select>
            </label>
            <label className="wide">
              Quantidade de moedas
              <input
                name="quantidade"
                type="number"
                min="1"
                max={Math.min(saldo, Number.MAX_SAFE_INTEGER)}
                step="1"
                inputMode="numeric"
                required
              />
            </label>
            <label className="wide">
              Motivo
              <textarea name="motivo" rows={4} maxLength={2000} required />
            </label>
          </div>
        </fieldset>
        <div className="form-actions">
          <button
            className="button primary"
            type="submit"
            disabled={operacao.busy || (!operacao.pendente && (!alunos?.length || saldo <= 0))}
          >
            {operacao.busy
              ? 'Confirmando…'
              : operacao.pendente
                ? 'Confirmar envio novamente'
                : 'Enviar moedas'}
          </button>
        </div>
      </form>
    </section>
  )
}

export default function ContaArea({
  sessao,
  versao,
  onRefresh,
}: {
  sessao: Sessao
  versao: number
  onRefresh: () => void
}) {
  const [conta, setConta] = useState<Conta | null>(null)
  const [erro, setErro] = useState<string | null>(null)
  const [versaoCarregada, setVersaoCarregada] = useState(-1)
  const carregando = versaoCarregada !== versao
  useEffect(() => {
    const abort = new AbortController()
    request<Conta>('/conta', { signal: abort.signal })
      .then((valor) => {
        if (!abort.signal.aborted) {
          setConta(valor)
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
  return (
    <div className="activity-stack">
      <div className="section-heading">
        <h2>Minha conta</h2>
        <button className="button secondary small-button" disabled={carregando} onClick={onRefresh}>
          Atualizar saldo e extrato
        </button>
      </div>
      {erro && (
        <p className="notice error" role="alert">
          {erro} Atualize para consultar os dados atuais.
        </p>
      )}
      {carregando && (
        <p role="status" className="hint">
          Atualizando conta…
        </p>
      )}
      {conta && (
        <>
          <div className="account-layout">
            <Saldo saldo={conta.saldo} />
            {sessao.perfil === 'PROFESSOR' ? (
              <EnviarMoedas sessao={sessao} saldo={conta.saldo} onSuccess={onRefresh} />
            ) : (
              <section className="account-note">
                <h2>Seu mérito abre possibilidades.</h2>
                <p>
                  Consulte as vantagens e troque suas moedas por benefícios das empresas parceiras.
                  Seus cupons ficam registrados no extrato.
                </p>
              </section>
            )}
          </div>
          <Extrato itens={conta.extrato} />
        </>
      )}
    </div>
  )
}
