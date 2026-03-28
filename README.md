# 🤖 Ciborg Store

Uma plataforma open source para **compilar APKs Android a partir de projetos ZIP** e publicá-los em uma loja estilo Play Store — com acessibilidade como prioridade.

Criado por **Natan** ([@Natanmiola369ops](https://github.com/Natanmiola369ops)) — desenvolvedor cego que acredita que tecnologia precisa ser acessível para todos.

---

## 🎯 Missão

> "Tornar esse mundo ainda pouco acessível em completamente usável — um app por vez."

---

## 📱 O que é

O **Ciborg Store** é composto por:

- **Plataforma Web** — Dashboard para upload de projetos ZIP e loja pública de apps
- **App Android Nativo (Java)** — Cliente mobile para a loja e gerenciamento de builds
- **Backend com GitHub Actions** — Compilação automática de APKs na nuvem

---

## 🚀 Funcionalidades

- ✅ Upload de projeto Android em ZIP
- ✅ Compilação automática via GitHub Actions
- ✅ Loja de apps estilo Play Store
- ✅ Download direto de APKs
- ✅ Dashboard de gerenciamento de builds
- ✅ App Android nativo em Java

---

## 🛠️ Tecnologias

- **Frontend/Web:** React (Base44)
- **Backend:** Deno (Base44 Functions)
- **Mobile:** Java (Android)
- **CI/CD:** GitHub Actions
- **Database:** Base44 Entities

---

## 📦 Estrutura do Projeto

```
android-project/
├── app/
│   └── src/main/
│       ├── java/com/ciborg/store/
│       │   ├── activities/     # Telas do app
│       │   ├── adapters/       # RecyclerView adapters
│       │   ├── api/            # Cliente da API
│       │   └── models/         # Modelos de dados
│       └── res/
│           ├── layout/         # Layouts XML
│           └── values/         # Cores e temas
```

---

## ▶️ Como usar

1. Clone o repositório
2. Abra a pasta `android-project` no **Android Studio**
3. Aguarde o Gradle sync
4. Rode no emulador ou device físico

---

## ♿ Acessibilidade

Este projeto tem como valor central a acessibilidade digital. Contribuições que melhorem o suporte a TalkBack, navegação por teclado, contraste e fontes são especialmente bem-vindas!

---

## 🤝 Contribuindo

Pull requests são bem-vindos! Abra uma issue antes para discutir mudanças maiores.

---

## 📄 Licença

MIT — livre para usar, modificar e distribuir.

---

*Feito com ❤️ e propósito.*
