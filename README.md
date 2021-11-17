# RealWorld Example App

> ### Datahike + Clojure codebase containing real world examples (CRUD, auth, advanced patterns, etc) that adheres to the [RealWorld](https://github.com/gothinkster/realworld) spec and API.

This codebase was created to demonstrate a fully fledged fullstack application built with **Datahike**, **Postgres** and **Reitit** including CRUD operations, authentication, routing, pagination, and more.

We've gone to great lengths to adhere to the **Clojure** community styleguides & best practices.

For more information on how to this works with other frontends/backends, head over to the [RealWorld](https://github.com/gothinkster/realworld) repo.

# Getting started

Clone the repository

```bash
https://github.com/learnuidev/clojure-realworld-immutable-stack-app.git
```

Install dependencies

```bash
lein
```

Start the repl

```bash
lein repl
```

Connect to repl using your favourite text editor

Start the app from `env/dev/user.clj` namespace by calling the `start!` function

Run tests

```bash
lein test
```

## API Specification

This application adheres to the api specifications set by the [Thinkster](https://github.com/gothinkster) team. This helps mix and match any backend with any other frontend without conflicts.

> [Full API Spec](https://github.com/gothinkster/realworld/tree/master/api)

More information regarding the project can be found here https://github.com/gothinkster/realworld
