# API Specification: User Management

Berikut adalah spesifikasi RESTful API untuk fitur *User Management*. Spesifikasi ini menggunakan format standar JSON dengan *wrapper* `data` untuk respons sukses dan `errors` untuk respons gagal. Autentikasi direpresentasikan menggunakan standar *header* `Authorization` dengan format token.

## 1. Register User
Mendaftarkan pengguna baru ke dalam sistem.

* **Endpoint:** `POST /api/users`
* **Request Header:**
  * `Content-Type: application/json`
  * `Accept: application/json`
* **Request Body:**
```json
{
  "username": "johndoe",
  "password": "password123",
  "name": "John Doe"
}
```
* **Response (201 Created):**
```json
{
  "data": {
    "username": "johndoe",
    "name": "John Doe"
  }
}
```
* **Response (400 Bad Request):**
```json
{
  "errors": "Username already exists"
}
```

---

## 2. Login User
Mengautentikasi pengguna dan mengembalikan token untuk akses endpoint yang membutuhkan autentikasi.

* **Endpoint:** `POST /api/users/login`
* **Request Header:**
  * `Content-Type: application/json`
  * `Accept: application/json`
* **Request Body:**
```json
{
  "username": "johndoe",
  "password": "password123"
}
```
* **Response (200 OK):**
```json
{
  "data": {
    "token": "uuid-atau-jwt-token-disini"
  }
}
```
* **Response (401 Unauthorized):**
```json
{
  "errors": "Username or password wrong"
}
```

---

## 3. Get User (Current)
Mengambil data profil pengguna yang sedang *login* berdasarkan token.

* **Endpoint:** `GET /api/users/current`
* **Request Header:**
  * `Accept: application/json`
  * `X-API-TOKEN: <token>`
* **Request Body:** (Kosong)
* **Response (200 OK):**
```json
{
  "data": {
    "username": "johndoe",
    "name": "John Doe"
  }
}
```
* **Response (401 Unauthorized):**
```json
{
  "errors": "Unauthorized"
}
```

---

## 4. Update User
Memperbarui data pengguna yang sedang *login* (menggunakan `PATCH` untuk pembaruan parsial).

* **Endpoint:** `PATCH /api/users/current`
* **Request Header:**
  * `Content-Type: application/json`
  * `Accept: application/json`
  * `X-API-TOKEN: <token>`
* **Request Body (Opsional, kirim field yang ingin diubah saja):**
```json
{
  "name": "John Doe Updated",
  "password": "newpassword456"
}
```
* **Response (200 OK):**
```json
{
  "data": {
    "username": "johndoe",
    "name": "John Doe Updated"
  }
}
```
* **Response (401 Unauthorized):**
```json
{
  "errors": "Unauthorized"
}
```

---

## 5. Logout User
Menghapus atau membatalkan sesi token pengguna yang sedang *login*.

* **Endpoint:** `DELETE /api/users/logout`
* **Request Header:**
  * `Accept: application/json`
  * `X-API-TOKEN: <token>`
* **Request Body:** (Kosong)
* **Response (200 OK):**
```json
{
  "data": "OK"
}
```
* **Response (401 Unauthorized):**
```json
{
  "errors": "Unauthorized"
}
```
