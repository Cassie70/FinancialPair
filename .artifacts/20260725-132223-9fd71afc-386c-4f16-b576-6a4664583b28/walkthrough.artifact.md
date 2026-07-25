# Almacenamiento en Caché de Logos y Persistencia Offline

He implementado una solución de dos capas para asegurar que los logos de los movimientos se muestren incluso sin conexión a internet.

## Cambios Realizados

### 1. Caché de Imágenes con Coil
En [FinancialPairApplication.kt](file:///C:/Users/Cass/Desktop/FinancialPair/app/src/main/java/com/juni7/financialpair/FinancialPairApplication.kt), he configurado el `ImageLoader` de Coil para utilizar un caché de disco persistente.
- Las imágenes SVG se guardan en el directorio de caché de la app (`image_cache`).
- He limitado el tamaño del caché al 2% del espacio disponible para ser eficiente.
- He activado `crossfade` para una transición más suave al cargar.

### 2. Persistencia de URLs en la Base de Datos
Para que la app sepa qué imagen cargar cuando no hay internet, ahora guardamos la URL directamente en la entidad `Topic`.
- **Entidad**: Añadido campo `logoUrl` a [Topic.kt](file:///C:/Users/Cass/Desktop/FinancialPair/app/src/main/java/com/juni7/financialpair/data/entity/Topic.kt).
- **DAO/Repository**: Creado método `updateLogoUrl` en [TopicDao.kt](file:///C:/Users/Cass/Desktop/FinancialPair/app/src/main/java/com/juni7/financialpair/data/dao/TopicDao.kt) y [TopicRepository.kt](file:///C:/Users/Cass/Desktop/FinancialPair/app/src/main/java/com/juni7/financialpair/data/repository/TopicRepository.kt).

### 3. Lógica del ViewModel
En [MovementsScreenViewModel.kt](file:///C:/Users/Cass/Desktop/FinancialPair/app/src/main/java/com/juni7/financialpair/ui/screens/movements/MovementsScreenViewModel.kt), he actualizado `fetchMissingLogos` para:
1.  **Cargar desde la DB**: Si el tema ya tiene una `logoUrl` guardada, se usa inmediatamente (permitiendo que Coil la recupere de su caché de disco).
2.  **Guardar al descargar**: Cuando se obtiene una URL nueva de Firebase, se persiste automáticamente en la base de datos para futuros usos offline.

## Verificación

- [x] **Compilación**: El proyecto compila correctamente con los cambios en Room y Coil.
- [ ] **Manual (Pendiente por el usuario)**:
    1. Abrir la app con internet para descargar los logos.
    2. Cerrar la app y activar el modo avión.
    3. Reabrir la app y verificar que los logos sigan apareciendo.
