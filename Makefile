check-format:
	lein cljfmt check

format:
	lein cljfmt check || lein cljfmt fix

