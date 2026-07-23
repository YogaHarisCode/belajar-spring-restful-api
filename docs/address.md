# API Specification: Address Management

Spesifikasi RESTful API untuk fitur **Address Management**. Data alamat (*address*) terikat secara langsung dengan data kontak (*contact*).

Seluruh endpoint pada modul ini membutuhkan autentikasi menggunakan **Request Header wajib**:
`X-TOKEN-API: <token>`

---

## 1. Create Address
Membuat data alamat baru untuk kontak tertentu.

* **Endpoint:** `POST /api/contacts/{contactId}/addresses`
* **Request Header:**
  * `X-TOKEN-API: <token>` *(Mandatory)*
  * `Content-Type: application/json`
  * `Accept: application/json`
* **Request Body:**
```json
{
  "street": "Jalan Sudirman No. 45",
  "city": "Jakarta Selatan",
  "province": "DKI Jakarta",
  "country": "Indonesia",
  "postal_code": "12190"
}
```
* **Response Body Success (201 Created):**
```json
{
  "data": {
    "id": "c12ebc99-9c0b-4ef8-bb6d-6bb9bd380b22",
    "street": "Jalan Sudirman No. 45",
    "city": "Jakarta Selatan",
    "province": "DKI Jakarta",
    "country": "Indonesia",
    "postal_code": "12190"
  }
}
```
* **Response Body Failed (400 Bad Request):**
```json
{
  "errors": "Country is required"
}
```
* **Response Body Failed (404 Not Found - Contact Tidak Ditemukan):**
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

## 2. Get Address
Mengambil detail data alamat tertentu milik kontak tertentu.

* **Endpoint:** `GET /api/contacts/{contactId}/addresses/{addressId}`
* **Request Header:**
  * `X-TOKEN-API: <token>` *(Mandatory)*
  * `Accept: application/json`
* **Request Body:** (Kosong)
* **Response Body Success (200 OK):**
```json
{
  "data": {
    "id": "c12ebc99-9c0b-4ef8-bb6d-6bb9bd380b22",
    "street": "Jalan Sudirman No. 45",
    "city": "Jakarta Selatan",
    "province": "DKI Jakarta",
    "country": "Indonesia",
    "postal_code": "12190"
  }
}
```
* **Response Body Failed (404 Not Found - Contact atau Address Tidak Ditemukan):**
```json
{
  "errors": "Address is not found"
}
```
* **Response Body Failed (401 Unauthorized):**
```json
{
  "errors": "Unauthorized"
}
```

---

## 3. Update Address
Memperbarui data alamat milik kontak tertentu.

* **Endpoint:** `PUT /api/contacts/{contactId}/addresses/{addressId}`
* **Request Header:**
  * `X-TOKEN-API: <token>` *(Mandatory)*
  * `Content-Type: application/json`
  * `Accept: application/json`
* **Request Body:**
```json
{
  "street": "Jalan Gatot Subroto No. 10",
  "city": "Jakarta Selatan",
  "province": "DKI Jakarta",
  "country": "Indonesia",
  "postal_code": "12930"
}
```
* **Response Body Success (200 OK):**
```json
{
  "data": {
    "id": "c12ebc99-9c0b-4ef8-bb6d-6bb9bd380b22",
    "street": "Jalan Gatot Subroto No. 10",
    "city": "Jakarta Selatan",
    "province": "DKI Jakarta",
    "country": "Indonesia",
    "postal_code": "12930"
  }
}
```
* **Response Body Failed (400 Bad Request):**
```json
{
  "errors": "Postal code is invalid"
}
```
* **Response Body Failed (404 Not Found - Contact atau Address Tidak Ditemukan):**
```json
{
  "errors": "Address is not found"
}
```
* **Response Body Failed (401 Unauthorized):**
```json
{
  "errors": "Unauthorized"
}
```

---

## 4. List Address
Mengambil seluruh daftar alamat milik kontak tertentu.

* **Endpoint:** `GET /api/contacts/{contactId}/addresses`
* **Request Header:**
  * `X-TOKEN-API: <token>` *(Mandatory)*
  * `Accept: application/json`
* **Request Body:** (Kosong)
* **Response Body Success (200 OK):**
```json
{
  "data": [
    {
      "id": "c12ebc99-9c0b-4ef8-bb6d-6bb9bd380b22",
      "street": "Jalan Sudirman No. 45",
      "city": "Jakarta Selatan",
      "province": "DKI Jakarta",
      "country": "Indonesia",
      "postal_code": "12190"
    },
    {
      "id": "d23fbc99-9c0b-4ef8-bb6d-7cc9bd380c33",
      "street": "Jalan MH Thamrin No. 1",
      "city": "Jakarta Pusat",
      "province": "DKI Jakarta",
      "country": "Indonesia",
      "postal_code": "10310"
    }
  ]
}
```
* **Response Body Failed (404 Not Found - Contact Tidak Ditemukan):**
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

## 5. Remove Address
Menghapus data alamat milik kontak tertentu.

* **Endpoint:** `DELETE /api/contacts/{contactId}/addresses/{addressId}`
* **Request Header:**
  * `X-TOKEN-API: <token>` *(Mandatory)*
  * `Accept: application/json`
* **Request Body:** (Kosong)
* **Response Body Success (200 OK):**
```json
{
  "data": "OK"
}
```
* **Response Body Failed (404 Not Found - Contact atau Address Tidak Ditemukan):**
```json
{
  "errors": "Address is not found"
}
```
* **Response Body Failed (401 Unauthorized):**
```json
{
  "errors": "Unauthorized"
}
```
