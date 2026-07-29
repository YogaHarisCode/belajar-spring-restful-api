# API Specification: Contact Management

Spesifikasi RESTful API untuk fitur **Contact Management**. 

Seluruh endpoint pada modul ini membutuhkan autentikasi menggunakan **Request Header wajib**:
`X-API-TOKEN: <token>`

---

## 1. Create Contact
Membuat data kontak baru.

* **Endpoint:** `POST /api/contacts`
* **Request Header:**
  * `X-API-TOKEN: <token>` *(Mandatory)*
  * `Content-Type: application/json`
  * `Accept: application/json`
* **Request Body:**
```json
{
  "first_name": "John",
  "last_name": "Doe",
  "email": "johndoe@example.com",
  "phone": "081234567890"
}
```
* **Response Body Success (201 Created):**
```json
{
  "data": {
    "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
    "first_name": "John",
    "last_name": "Doe",
    "email": "johndoe@example.com",
    "phone": "081234567890"
  }
}
```
* **Response Body Failed (400 Bad Request):**
```json
{
  "errors": "Email format is invalid"
}
```
* **Response Body Failed (401 Unauthorized):**
```json
{
  "errors": "Unauthorized"
}
```

---

## 2. Get Contact
Mengambil detail data kontak berdasarkan ID (UUID).

* **Endpoint:** `GET /api/contacts/{contactId}`
* **Request Header:**
  * `X-API-TOKEN: <token>` *(Mandatory)*
  * `Accept: application/json`
* **Request Body:** (Kosong)
* **Response Body Success (200 OK):**
```json
{
  "data": {
    "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
    "first_name": "John",
    "last_name": "Doe",
    "email": "johndoe@example.com",
    "phone": "081234567890"
  }
}
```
* **Response Body Failed (404 Not Found):**
```json
{
  "errors": "Contact is not found"
}
```
* **Response Body Failed (401 Unauthorized):**
```json
{
  "errors": "Unauthorized"
}
```

---

## 3. Update Contact
Memperbarui data kontak berdasarkan ID (UUID).

* **Endpoint:** `PUT /api/contacts/{contactId}`
* **Request Header:**
  * `X-API-TOKEN: <token>` *(Mandatory)*
  * `Content-Type: application/json`
  * `Accept: application/json`
* **Request Body:**
```json
{
  "first_name": "John",
  "last_name": "Smith",
  "email": "johnsmith@example.com",
  "phone": "089876543210"
}
```
* **Response Body Success (200 OK):**
```json
{
  "data": {
    "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
    "first_name": "John",
    "last_name": "Smith",
    "email": "johnsmith@example.com",
    "phone": "089876543210"
  }
}
```
* **Response Body Failed (400 Bad Request):**
```json
{
  "errors": "First name is required"
}
```
* **Response Body Failed (404 Not Found):**
```json
{
  "errors": "Contact is not found"
}
```
* **Response Body Failed (401 Unauthorized):**
```json
{
  "errors": "Unauthorized"
}
```

---

## 4. Search Contact
Mencari data kontak pengguna berdasarkan nama, email, atau telepon dengan paginasi.

* **Endpoint:** `GET /api/contacts`
* **Query Parameters (Opsional):**
  * `name` (string): Pencarian nama depan/belakang
  * `email` (string): Pencarian email
  * `phone` (string): Pencarian nomor telepon
  * `page` (integer, default: 0): Nomor halaman
  * `size` (integer, default: 10): Jumlah data per halaman
* **Request Header:**
  * `X-API-TOKEN: <token>` *(Mandatory)*
  * `Accept: application/json`
* **Request Body:** (Kosong)
* **Response Body Success (200 OK):**
```json
{
  "data": [
    {
      "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
      "first_name": "John",
      "last_name": "Smith",
      "email": "johnsmith@example.com",
      "phone": "089876543210"
    }
  ],
  "paging": {
    "current_page": 1,
    "total_page": 1,
    "size": 10
  }
}
```
* **Response Body Failed (401 Unauthorized):**
```json
{
  "errors": "Unauthorized"
}
```

---

## 5. Remove Contact
Menghapus data kontak berdasarkan ID (UUID).

* **Endpoint:** `DELETE /api/contacts/{contactId}`
* **Request Header:**
  * `X-API-TOKEN: <token>` *(Mandatory)*
  * `Accept: application/json`
* **Request Body:** (Kosong)
* **Response Body Success (200 OK):**
```json
{
  "data": "OK"
}
```
* **Response Body Failed (404 Not Found):**
```json
{
  "errors": "Contact is not found"
}
```
* **Response Body Failed (401 Unauthorized):**
```json
{
  "errors": "Unauthorized"
}
```
