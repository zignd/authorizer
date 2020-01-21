run-dev:
	env $$(cat ENV) lein run

run-repl:
	env $$(cat ENV) lein update-in :dependencies conj \[nrepl\ \"0.6.0\"\] -- update-in :plugins conj \[cider/cider-nrepl\ \"0.23.0-SNAPSHOT\"\] -- repl :headless :host localhost

check-format:
	lein cljfmt check

format:
	lein cljfmt check || lein cljfmt fix

create-db-dev:
	docker exec -it postgres psql -U postgres -c "CREATE DATABASE twito;"

migrate-db:
	env $$(cat db/ENV) dbmate migrate
