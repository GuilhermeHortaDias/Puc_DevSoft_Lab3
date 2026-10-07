import { test, expect } from '@playwright/test'

for (const perfil of ['aluno', 'empresa'] as const) {
  test(`${perfil}: cadastro, sessão após recarregar, edição e inativação`, async ({ page }) => {
    const unico = Date.now().toString()
    const login = `e2e-${perfil}-${unico}`
    await page.goto('/')
    await page
      .getByRole('button', {
        name: perfil === 'aluno' ? 'Criar conta de aluno' : 'Cadastrar empresa',
        exact: true,
      })
      .click()
    await page
      .getByLabel(perfil === 'aluno' ? 'Nome completo' : 'Nome da empresa', { exact: true })
      .fill(`Teste ${perfil}`)
    await page.getByLabel('Email', { exact: true }).fill(`${login}@example.com`)
    await page.getByLabel('Login', { exact: true }).fill(login)
    await page.getByLabel('Senha', { exact: true }).fill('Teste123!')
    if (perfil === 'aluno') {
      await page.getByLabel('CPF', { exact: true }).fill(unico.slice(-11))
      await page.getByLabel('RG', { exact: true }).fill('MG123456')
      await page.getByLabel('Endereço', { exact: true }).fill('Rua de Testes, 10')
      await page.getByLabel('Curso', { exact: true }).fill('Engenharia de Software')
      await page.getByLabel('Instituição', { exact: true }).selectOption('1')
    }
    await page.getByRole('button', { name: 'Concluir cadastro', exact: true }).click()
    await expect(page.getByRole('heading', { name: 'Meu cadastro', exact: true })).toBeVisible()
    await page.reload()
    await expect(page.getByText(`Teste ${perfil}`, { exact: true }).first()).toBeVisible()
    await page.getByRole('button', { name: 'Editar cadastro', exact: true }).click()
    await page
      .getByLabel(perfil === 'aluno' ? 'Nome completo' : 'Nome da empresa', { exact: true })
      .fill(`Teste ${perfil} atualizado`)
    await page.getByRole('button', { name: 'Salvar alterações', exact: true }).click()
    await expect(
      page.getByText(`Teste ${perfil} atualizado`, { exact: true }).first(),
    ).toBeVisible()
    await page.getByRole('button', { name: 'Sair', exact: true }).click()
    await page.getByLabel('Login', { exact: true }).fill(login)
    await page.getByLabel('Senha', { exact: true }).fill('Teste123!')
    await page.getByRole('button', { name: 'Entrar', exact: true }).click()
    await expect(page.getByRole('heading', { name: 'Meu cadastro', exact: true })).toBeVisible()
    await page.getByRole('button', { name: 'Inativar cadastro', exact: true }).click()
    await page.getByRole('button', { name: 'Confirmar inativação', exact: true }).click()
    await expect(page.getByText('Cadastro inativado com sucesso.', { exact: true })).toBeVisible()
    await page.getByLabel('Login', { exact: true }).fill(login)
    await page.getByLabel('Senha', { exact: true }).fill('Teste123!')
    await page.getByRole('button', { name: 'Entrar', exact: true }).click()
    await expect(page.getByRole('alert')).toContainText('Login ou senha inválidos')
  })
}
