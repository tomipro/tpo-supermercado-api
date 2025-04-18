# TP Grupo 04: Supermercado

## Endpoints de Usuario
> Todos los endpoints (excepto login y registro) requieren autenticación JWT.  
> Agregar header: `Authorization: Bearer {token}` (setear en Postman o Insomnia).

### Autenticación
- El token se obtiene al hacer login.
- El rol de usuario se asigna automáticamente al registrarse.
- El rol de administrador se asigna manualmente en la base de datos.
- El administrador accede a todos los endpoints.
- El usuario accede solo a sus propios endpoints de usuario y dirección.
- El usuario no puede acceder a endpoints de administrador ni a datos de otros usuarios.

### POST /usuarios/login
Autentica usuario y devuelve JWT.
#### Request:
```json
{
  "username": "usuario1",
  "password": "1234"
}
```
#### Response:
```json
{
  "token": "jwt_token_aqui",
  "usuario": {
    "id": 1,
    "username": "usuario1",
    "email": "usuario1@mail.com",
    "nombre": "Juan",
    "apellido": "Perez",
    "rol": "USER"
  }
}
```

---

### POST /usuarios
Crea un nuevo usuario.
#### Request:
```json
{
  "username": "usuario2",
  "email": "usuario2@mail.com",
  "password": "abcd",
  "nombre": "Ana",
  "apellido": "Gomez",
  "rol": "USER"
}
```
#### Response:
```json
{
  "id": 2,
  "username": "usuario2",
  "email": "usuario2@mail.com",
  "nombre": "Ana",
  "apellido": "Gomez",
  "rol": "USER",
  "fecha_registro": "2024-05-01T12:00:00"
}
```

---

### GET /usuarios
Lista todos los usuarios (sin password).
#### Response:
```json
[
  {
    "id": 1,
    "username": "usuario1",
    "email": "usuario1@mail.com",
    "nombre": "Juan",
    "apellido": "Perez",
    "rol": "USER",
    "fecha_registro": "2024-05-01T12:00:00"
  }
]
```

---

### GET /usuarios/{id}
Obtiene usuario por ID (sin password).

---

### PUT /usuarios/{id}
Actualiza usuario (reemplazo total, sin password).
#### Request:
```json
{
  "username": "usuario1",
  "email": "usuario1@mail.com",
  "nombre": "Juan",
  "apellido": "Perez",
  "rol": "USER"
}
```

---

### PATCH /usuarios/{id}
Actualiza parcialmente usuario (sin password).
#### Request:
```json
{
  "nombre": "Juan Carlos"
}
```

---

### PUT /usuarios/password
Cambia la contraseña del usuario autenticado.
#### Request:
```json
{
  "contrasenaActual": "1234",
  "nuevaContrasena": "nueva123"
}
```
#### Response:
`"La contraseña fue actualizada correctamente."`

---

### DELETE /usuarios/{id}
Elimina usuario por ID.

---

### GET /usuarios/exists/username/{username}
Verifica si existe un usuario por username.  
#### Response: `true` o `false`

---

### GET /usuarios/exists/email/{email}
Verifica si existe un usuario por email.  
#### Response: `true` o `false`

---

### GET /usuarios/rol/{rol}
Lista usuarios por rol.

---

### GET /usuarios/me
Devuelve el perfil del usuario autenticado.

---

## Endpoints de Dirección

> Todos requieren autenticación JWT.

### GET /direcciones
Lista todas las direcciones del usuario autenticado.
#### Response:
```json
[
  {
    "id": 1,
    "calle": "Av. Siempre Viva",
    "numero": "742",
    "pisoDepto": "2B",
    "ciudad": "Springfield",
    "provincia": "Buenos Aires",
    "codigoPostal": "1234",
    "tipoVivienda": "departamento"
  }
]
```

---

### POST /direcciones
Crea una nueva dirección para el usuario autenticado.
#### Request:
```json
{
  "calle": "Av. Siempre Viva",
  "numero": "742",
  "pisoDepto": "2B",
  "ciudad": "Springfield",
  "provincia": "Buenos Aires",
  "codigoPostal": "1234",
  "tipoVivienda": "departamento"
}
```
#### Response:
```json
{
  "id": 2,
  "calle": "Av. Siempre Viva",
  "numero": "742",
  "pisoDepto": "2B",
  "ciudad": "Springfield",
  "provincia": "Buenos Aires",
  "codigoPostal": "1234",
  "tipoVivienda": "departamento"
}
```

---

### PUT /direcciones/{id}
Actualiza una dirección (solo si pertenece al usuario).
#### Request:
```json
{
  "calle": "Av. Siempre Viva",
  "numero": "742",
  "pisoDepto": "3C",
  "ciudad": "Springfield",
  "provincia": "Buenos Aires",
  "codigoPostal": "1234",
  "tipoVivienda": "departamento"
}
```

---

### DELETE /direcciones/{id}
Elimina una dirección (solo si pertenece al usuario).

---

**Notas:**
- No envíes el campo `usuario` en el body de dirección, se asigna automáticamente.
- Si intentas modificar/eliminar una dirección que no te pertenece, recibirás 403 Forbidden.
- Si no estás autenticado, recibirás 401 Unauthorized.
- Si el usuario no existe, recibirás 404 Not Found.
- Si el rol no es válido, recibirás 400 Bad Request.
- Si el usuario ya existe, recibirás 409 Conflict.
- Si el email no es válido, recibirás 400 Bad Request.
- Si la contraseña no cumple con los requisitos, recibirás 400 Bad Request.
- Si el token es inválido o ha expirado, recibirás 401 Unauthorized.
- 
--------------------------------------------------------------------------------

## Endpoints de Categoría

### Autenticación y permisos

- El usuario o No usuario puede consultar categorías, pero no puede modificarlas.
- Solo el administrador puede crear, editar o eliminar categorías.

## Modelo de solicitud

### `CategoryRequest`
- nombre: nombre de la categoría (String)
- parentId: ID de la categoría padre (Integer) - puede ser null si es una categoria principal.

### Endpoints:
   
    ==========

### GET /categorias
---------------
Lista todas las categorías y subcategorías.

Acceso: ADMIN, USER y NoUser

Query params opcionales:
- page (int): número de página (default: 0)
- size (int): cantidad de elementos por página (default: 10)

//categorias?page=0&size=5 o //categorias
Response:
[
  {
    "id": 1,
    "nombre": "Bebidas",
    "subcategorias": [
      {
        "id": 2,
        "nombre": "Gaseosas"
      }
    ]
  }
]

--------------------
### GET /categorias/{id}
---------------------
### Obtiene una categoría por ID.

Acceso: ADMIN, USER y NoUser

Response:
{
  "id": 1,
  "nombre": "Alimentos",
  "subcategorias": [
    {
      "id": 5,
      "nombre": "Pastas"
    }
  ]
}

Errores:
- 404 Not Found: Categoría inexistente.

---

### POST /categorias
----------------
Crea una nueva categoría o subcategoría.

Acceso: Solo ADMIN

Request:
{
  "nombre": "Lácteos",
  "parentId": null
}

Response:
{
  "id": 3,
  "nombre": "Lácteos",
  "parent": null
}

Validaciones:
- parentId debe referenciar una categoría válida.
- El nombre no puede repetirse al mismo nivel.

Errores:
- 409 Conflict: Ya existe una categoría con ese nombre.
---

#### PUT /categorias/{id}
---------------------
### Reemplaza completamente una categoría existente.

Acceso: Solo ADMIN

Request:
{
  "nombre": "Snacks",
  "parentId": null
}

Response:
{
  "id": 4,
  "nombre": "Snacks",
  "parent": null
}

Errores: No existe la categoria que se quiere actualizar
---

