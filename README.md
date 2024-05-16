# fluffy-waffle
Проект второго семестра \\
Ссылка на [google docs](https://docs.google.com/document/d/1pKbKIoEZukBwIMyEB0exbMJwJeXZ3vJbxzTvFgZum4s/edit), где расписаны начальные заметки \\
Текущее open-api json у главного микросервиса:

{
    "openapi": "3.0.1",
    "info": {
        "title": "OpenAPI definition",
        "version": "v0"
    },
    "servers": [
        {
            "url": "http://localhost:8080",
            "description": "Generated server url"
        }
    ],
    "paths": {
        "/api/user": {
            "post": {
                "tags": [
                    "user-controller"
                ],
                "operationId": "registry",
                "requestBody": {
                    "content": {
                        "application/json": {
                            "schema": {
                                "$ref": "#/components/schemas/UserRegistryRequest"
                            }
                        }
                    },
                    "required": true
                },
                "responses": {
                    "200": {
                        "description": "OK",
                        "content": {
                            "*/*": {
                                "schema": {
                                    "type": "string",
                                    "format": "uuid"
                                }
                            }
                        }
                    }
                }
            }
        },
        "/api/lot": {
            "post": {
                "tags": [
                    "lot-controller"
                ],
                "operationId": "createLot",
                "requestBody": {
                    "content": {
                        "application/json": {
                            "schema": {
                                "$ref": "#/components/schemas/LotCreateRequest"
                            }
                        }
                    },
                    "required": true
                },
                "responses": {
                    "200": {
                        "description": "OK",
                        "content": {
                            "*/*": {
                                "schema": {
                                    "type": "integer",
                                    "format": "int64"
                                }
                            }
                        }
                    }
                }
            }
        },
        "/api/bet/make": {
            "post": {
                "tags": [
                    "bet-controller"
                ],
                "operationId": "createBet",
                "requestBody": {
                    "content": {
                        "application/json": {
                            "schema": {
                                "$ref": "#/components/schemas/BetMakingRequest"
                            }
                        }
                    },
                    "required": true
                },
                "responses": {
                    "200": {
                        "description": "OK",
                        "content": {
                            "*/*": {
                                "schema": {
                                    "type": "integer",
                                    "format": "int64"
                                }
                            }
                        }
                    }
                }
            }
        },
        "/api/user/{id}": {
            "get": {
                "tags": [
                    "user-controller"
                ],
                "operationId": "getLotsByUser",
                "parameters": [
                    {
                        "name": "id",
                        "in": "path",
                        "required": true,
                        "schema": {
                            "type": "string",
                            "format": "uuid"
                        }
                    }
                ],
                "responses": {
                    "200": {
                        "description": "OK",
                        "content": {
                            "*/*": {
                                "schema": {
                                    "type": "object",
                                    "additionalProperties": {
                                        "type": "array",
                                        "items": {
                                            "$ref": "#/components/schemas/Lot"
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            "delete": {
                "tags": [
                    "user-controller"
                ],
                "operationId": "deleteUser",
                "parameters": [
                    {
                        "name": "id",
                        "in": "path",
                        "required": true,
                        "schema": {
                            "type": "string",
                            "format": "uuid"
                        }
                    }
                ],
                "responses": {
                    "200": {
                        "description": "OK"
                    }
                }
            }
        },
        "/api/lot/{id}": {
            "get": {
                "tags": [
                    "lot-controller"
                ],
                "operationId": "getLotById",
                "parameters": [
                    {
                        "name": "id",
                        "in": "path",
                        "required": true,
                        "schema": {
                            "type": "integer",
                            "format": "int64"
                        }
                    }
                ],
                "responses": {
                    "200": {
                        "description": "OK",
                        "content": {
                            "*/*": {
                                "schema": {
                                    "$ref": "#/components/schemas/Lot"
                                }
                            }
                        }
                    }
                }
            }
        },
        "/api/bet/{id}": {
            "get": {
                "tags": [
                    "bet-controller"
                ],
                "operationId": "getBetById",
                "parameters": [
                    {
                        "name": "id",
                        "in": "path",
                        "required": true,
                        "schema": {
                            "type": "integer",
                            "format": "int64"
                        }
                    }
                ],
                "responses": {
                    "200": {
                        "description": "OK",
                        "content": {
                            "*/*": {
                                "schema": {
                                    "$ref": "#/components/schemas/Bet"
                                }
                            }
                        }
                    }
                }
            }
        }
    },
    "components": {
        "schemas": {
            "UserRegistryRequest": {
                "type": "object",
                "properties": {
                    "login": {
                        "type": "string"
                    },
                    "firstName": {
                        "type": "string"
                    },
                    "lastName": {
                        "type": "string"
                    },
                    "password": {
                        "type": "string"
                    },
                    "email": {
                        "type": "string"
                    }
                }
            },
            "DTOMoney": {
                "type": "object",
                "properties": {
                    "integerPart": {
                        "type": "integer",
                        "format": "int64"
                    },
                    "decimalPart": {
                        "type": "integer",
                        "format": "int64"
                    },
                    "currency": {
                        "type": "string",
                        "enum": [
                            "RUB",
                            "USD",
                            "EUR"
                        ]
                    }
                }
            },
            "LotCreateRequest": {
                "type": "object",
                "properties": {
                    "userId": {
                        "type": "string",
                        "format": "uuid"
                    },
                    "initialPrice": {
                        "$ref": "#/components/schemas/DTOMoney"
                    },
                    "minimumIncrease": {
                        "$ref": "#/components/schemas/DTOMoney"
                    },
                    "startTime": {
                        "type": "string",
                        "format": "date-time"
                    },
                    "finishTime": {
                        "type": "string",
                        "format": "date-time"
                    },
                    "description": {
                        "type": "string"
                    },
                    "images": {
                        "type": "array",
                        "items": {
                            "type": "string"
                        }
                    }
                }
            },
            "BetMakingRequest": {
                "type": "object",
                "properties": {
                    "userId": {
                        "type": "string",
                        "format": "uuid"
                    },
                    "lotId": {
                        "type": "integer",
                        "format": "int64"
                    },
                    "value": {
                        "$ref": "#/components/schemas/DTOMoney"
                    }
                }
            },
            "Bet": {
                "type": "object",
                "properties": {
                    "id": {
                        "type": "integer",
                        "format": "int64"
                    },
                    "user": {
                        "$ref": "#/components/schemas/User"
                    },
                    "lot": {
                        "$ref": "#/components/schemas/Lot"
                    },
                    "value": {
                        "$ref": "#/components/schemas/Money"
                    }
                }
            },
            "Lot": {
                "type": "object",
                "properties": {
                    "id": {
                        "type": "integer",
                        "format": "int64"
                    },
                    "user": {
                        "$ref": "#/components/schemas/User"
                    },
                    "initialPrice": {
                        "$ref": "#/components/schemas/Money"
                    },
                    "minimumIncrease": {
                        "$ref": "#/components/schemas/Money"
                    },
                    "startTime": {
                        "type": "string",
                        "format": "date-time"
                    },
                    "finishTime": {
                        "type": "string",
                        "format": "date-time"
                    },
                    "description": {
                        "type": "string"
                    },
                    "images": {
                        "type": "array",
                        "items": {
                            "type": "string"
                        }
                    },
                    "lotState": {
                        "type": "string",
                        "enum": [
                            "NOT_SOLD",
                            "IN_PROGRESS",
                            "SOLD",
                            "REJECTED",
                            "UNSOLD"
                        ]
                    },
                    "lotBets": {
                        "type": "array",
                        "items": {
                            "$ref": "#/components/schemas/Bet"
                        }
                    }
                }
            },
            "Money": {
                "required": [
                    "currency",
                    "decimalPart",
                    "integerPart"
                ],
                "type": "object",
                "properties": {
                    "integerPart": {
                        "type": "integer",
                        "format": "int64"
                    },
                    "decimalPart": {
                        "type": "integer",
                        "format": "int64"
                    },
                    "currency": {
                        "type": "string",
                        "enum": [
                            "RUB",
                            "USD",
                            "EUR"
                        ]
                    }
                }
            },
            "User": {
                "required": [
                    "email",
                    "firstName",
                    "lastName",
                    "login",
                    "passwordHash"
                ],
                "type": "object",
                "properties": {
                    "id": {
                        "type": "string",
                        "format": "uuid"
                    },
                    "login": {
                        "type": "string"
                    },
                    "firstName": {
                        "type": "string"
                    },
                    "lastName": {
                        "type": "string"
                    },
                    "passwordHash": {
                        "type": "string"
                    },
                    "roles": {
                        "uniqueItems": true,
                        "type": "array",
                        "items": {
                            "type": "string",
                            "enum": [
                                "ADMIN",
                                "USER"
                            ]
                        }
                    },
                    "email": {
                        "type": "string"
                    },
                    "lots": {
                        "type": "array",
                        "items": {
                            "$ref": "#/components/schemas/Lot"
                        }
                    },
                    "bets": {
                        "type": "array",
                        "items": {
                            "$ref": "#/components/schemas/Bet"
                        }
                    }
                }
            }
        }
    }
}