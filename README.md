# MyNotesApp


## Sobre o Projeto

Este aplicativo foi desenvolvido para simplificar o gerenciamento de notas pessoais, permitindo um controle rápido e eficaz através de uma interface fácil e moderna. O MyNotesApp oferece funcionalidades para criar, editar, excluir e visualizar notas, com suporte para organização e filtragem com  gerenciamento de usuários, permitindo que cada usuário tenha suas próprias notas.

## Estrutura do Projeto

manifests # Configurações do projeto.
/java # Código-fonte principal do aplicativo.
/database # Implementação do Banco de dados.
/extensions # Funções de extensão em Kotlin para conversão de tipos de dados.
/preferences # Implementação do DataStore para armazenamento de preferências do usuário.
/model # Entidades.
/ui # Activities.
/res # Layouts, imagens, fontes, menus, etc.

## Tecnologias Utilizadas

- **Android Studio**: Ambiente de desenvolvimento integrado (IDE) utilizado para o desenvolvimento do aplicativo.

- **Room**: Biblioteca para persistência de dados que utiliza SQLite para armazenar informações localmente no dispositivo.

-  **SQLite**: Banco de dados relacional utilizado em conjunto com o Room para armazenamento de dados persistentes.

- **Data Binding**: Facilita a vinculação das views utilizadas nos layouts (ConstraintLayout).
  
-  **DataStore**: Solução para armazenamento de preferências e configurações do usuário..

- **Coroutines**: Utilizadas para realizar operações assíncronas, garantindo que as operações do banco de dados sejam realizadas em threads separadas da principal.

- **RecyclerView**: Utilizado para exibir listas de itens de forma eficiente, com suporte a animações e gerenciamento de grandes conjuntos de dados.

- **Kotlin**: Linguagem de programação utilizada em todo o projeto, oferecendo uma sintaxe concisa e recursos modernos para o desenvolvimento Android.



## Funcionalidades

- **Gerenciamento de Notas**: Criação, edição, exclusão e visualização de notas.
- **Organização**: Opções para ordenar notas por data de criação, data de modificação ou ordem alfabética.
- **Pesquisa**: Filtragem de notas com base em consultas de texto.
- **Autenticação do Usuário**: Tela de login e cadastro para gerenciar acessos.
- **Perfil do usuário**: Tela de perfil onde os usuários podem editar seu nome e alterar a senha.
- **Notas Vinculadas ao Usuário**:: Cada usuário visualiza e gerencia apenas suas próprias notas.
- **Interface Moderna**: Layouts responsivos e animações para uma experiência de usuário suave.
- **Armazenamento de Preferências**: Utilização do DataStore para gerenciar preferências e configurações do usuário.
- **Migrações**: Implementação de migrações para manter a integridade dos dados durante as atualizações do aplicativo.

## Como Executar

1. **Clone o Repositório**
   ```bash
   git clone https://github.com/caiquefirmino7/MyNotesApp.git
   


### Capturas de Tela

<table align="center">
  <tr>
    <td align="center">
      <img src="screenshots/telainicialnota.png" width="150" alt="Tela Inicial Nota">
    </td>
    <td align="center">
      <img src="screenshots/telausuario.png" width="150" alt="Tela Usuário">
    </td>
    <td align="center">
      <img src="screenshots/telacadastro.png" width="150" alt="Tela Cadastro">
    </td>
    <td align="center">
      <img src="screenshots/telacriarnota.png" width="150" alt="Tela Criar Nota">
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="screenshots/teladetalhenota.png" width="150" alt="Tela Detalhe Nota">
    </td>
    <td align="center">
      <img src="screenshots/telanota.png" width="150" alt="Tela Nota">
    </td>
    <td align="center">
      <img src="screenshots/telanotaescolha.png" width="150" alt="Tela Nota Escolha">
    </td>
    <td align="center">
      <img src="screenshots/telaperfil.png" width="150" alt="Tela Perfil">
    </td>
  </tr>
</table>


