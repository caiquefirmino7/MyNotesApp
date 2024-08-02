# MyNotesApp


## Sobre o Projeto

Este aplicativo foi desenvolvido para simplificar o gerenciamento de notas pessoais, permitindo um controle rápido e eficaz através de uma interface fácil e moderna. O MyNotesApp oferece funcionalidades para criar, editar, excluir e visualizar notas, com suporte para organização e filtragem.

## Estrutura do Projeto

manifests # Configurações do projeto.
/java # Código-fonte principal do aplicativo.
/database # Implementação do Banco de dados.
/extensions # Funções de extensão em Kotlin para conversão de tipos de dados.
/model # Entidades.
/ui # Interfaces do usuário.
/tests # Testes automatizados, garantindo a qualidade.
/res # Layouts, imagens, fontes, menus, etc.

## Tecnologias Utilizadas

- **Android Studio**: Ambiente de desenvolvimento integrado (IDE) utilizado para o desenvolvimento do aplicativo.

- **Room**: Biblioteca para persistência de dados que utiliza SQLite para armazenar informações localmente no dispositivo.

- - **SQLite**: Banco de dados relacional utilizado em conjunto com o Room para armazenamento de dados persistentes.

- **Data Binding**: Facilita a vinculação das views utilizadas nos layouts (ConstraintLayout).

- **Coroutines**: Utilizadas para realizar operações assíncronas, garantindo que as operações do banco de dados sejam realizadas em threads separadas da principal.

- **RecyclerView**: Utilizado para exibir listas de itens de forma eficiente, com suporte a animações e gerenciamento de grandes conjuntos de dados.

- **Kotlin**: Linguagem de programação utilizada em todo o projeto, oferecendo uma sintaxe concisa e recursos modernos para o desenvolvimento Android.



## Funcionalidades

- **Gerenciamento de Notas**: Criação, edição, exclusão e visualização de notas.
- **Organização**: Opções para ordenar notas por data de criação, data de modificação ou ordem alfabética.
- **Pesquisa**: Filtragem de notas com base em consultas de texto.
- **Interface Moderna**: Layouts responsivos e animações para uma experiência de usuário suave.

## Como Executar

1. **Clone o Repositório**
   ```bash
   git clone https://github.com/caiquefirmino7/MyNotesApp.git
   

  ## Imagens e Vídeo

### Vídeo

<video width="600" controls>
  <source src="app/src/main/res/assets/video.mp4" type="video/mp4">
  Your browser does not support the video tag.
</video>

### Capturas de Tela

- **Splash Screen**:

  <img src="app/src/main/res/assets/splashscreen.png" width="300" alt="Splash Screen">

- **Tela Inicial**:

  <img src="app/src/main/res/assets/telainicial.jpeg" width="300" alt="Tela Inicial">

- **Criar Nota**:

  <img src="app/src/main/res/assets/criarnota.jpeg" width="300" alt="Criar Nota">

- **Notas**:

  <img src="app/src/main/res/assets/notas.jpeg" width="300" alt="Notas">

