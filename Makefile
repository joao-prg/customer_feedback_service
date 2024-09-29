GRADLE = ./gradlew
PROJECT_NAME = customer_feedback_service

ifneq (,$(wildcard .env))
    include .env
    export $(shell sed 's/=.*//' .env)
endif

.PHONY: build
build:
	$(GRADLE) build

.PHONY: test
test:
	$(GRADLE) test

.PHONY: run
run: start-db
	$(GRADLE) bootRun

.PHONY: clean
clean:
	$(GRADLE) clean

.PHONY: rebuild
rebuild: clean build

.PHONY: env
env:
	@echo $(ADMIN_EMAIL)
	@echo $(ADMIN_PASSWORD)
	@echo $(MONGO_USER)
	@echo $(MONGO_PASSWORD)

.PHONY: docker-build
docker-build:
	docker compose build

.PHONY: start-db
start-db:
	docker compose -f docker-compose-dev.yml up feedback_db -d

.PHONY: start-docker
start-docker:
	docker compose -f docker-compose-dev.yml up customer_feedback_service feedback_db -d

.PHONY: run-docker-tests
run-docker-tests:
	docker compose -f docker-compose-dev.yml up customer_feedback_service_test

.PHONY: stop-docker
stop-docker:
	docker compose -f docker-compose-dev.yml down
