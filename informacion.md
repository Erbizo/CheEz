# Informacion del proyecto - CheEz

## URL de Figma

[Enlace al proyecto en Figma](https://www.figma.com/design/vsWklK4pjIALVIoNKsiISB/Ajedrez?node-id=0-1&t=TLAFJOITXenaBw8V-1)

[Enlace al RailWay](https://cheez-production.up.railway.app/)
---

## 1. Guia de estilo y sistema de diseno

### Paleta de colores

| Nombre | Hex | Uso |
|---|---|---|
| Fondo principal | `#1F1F1F` | Fondo de la pagina |
| Superficie | `#2F2F2F` | Tarjetas y paneles |
| Borde sutil | `#3F3F3F` | Separadores de listas |
| Verde primario | `#23CF46` | Boton principal, exito |
| Verde hover | `#1EB93D` | Estado hover del boton verde |
| Rojo error | `#CF3232` | Boton destructivo, error |
| Rojo hover | `#B02A2A` | Estado hover del boton rojo |
| Azul acento | `#5A9FD4` | Alias en cabecera, turno activo |
| Blanco texto | `#FFFFFF` | Texto principal |
| Gris input | `#D1D1D1` | Fondo de campos de texto |
| Texto input | `#1F1F1F` | Texto dentro de campos de texto |
| Casilla clara | `#D9C3A0` | Casillas blancas del tablero |
| Casilla oscura | `#2B2B2B` | Casillas negras del tablero |

### Tipografia

| Uso | Fuente | Tamano | Peso |
|---|---|---|---|
| Titulo principal (logo) | Georgia, serif | 4rem (login), 3rem (resto) | Normal |
| Subtitulos de seccion | Georgia, serif | 1.5rem | Normal |
| Alias en cabecera | Georgia, serif | 1.5rem | Bold |
| Texto de lista | Georgia, serif | 1.2rem | Normal |
| Botones | Georgia, serif | 1rem | Bold |
| Campos de texto | Georgia, serif | 1.2rem | Normal |
| Mensaje de error | Georgia, serif | 0.9rem | Normal |
| Nombre jugador (juego) | Georgia, serif | clamp(1.2rem, 2.5vw, 2.2rem) | Normal |

### Jerarquia visual

1. **Nivel 1** - Logo/titulo: CheEz (mayor tamano, centrado)
2. **Nivel 2** - Subtitulos de seccion: Jugadores activos, Invitaciones
3. **Nivel 3** - Elementos de lista: alias de jugadores, nombres en partida
4. **Nivel 4** - Textos auxiliares: mensajes de error, estado de tablas

---

## 3. Definicion de componentes visuales

### Boton primario (verde)

- Fondo: `#23CF46`
- Texto: `#FFFFFF`, negrita
- Border-radius: `1.5rem` (pill)
- Padding: `0.75rem 2rem`
- Fuente: Georgia, 1rem

### Boton destructivo (rojo)

- Fondo: `#CF3232`
- Texto: `#FFFFFF`, negrita
- Border-radius: `1.5rem` (pill)
- Padding: `0.75rem 2rem`
- Fuente: Georgia, 1rem

### Boton icono (circular)

- Tamano: `2.5rem x 2.5rem`
- Border-radius: `50%`
- Verde (`#23CF46`) para aceptar
- Rojo (`#CF3232`) para rechazar
- Icono: texto Unicode (✓ / ✕)

### Campo de texto (input)

- Fondo: `#D1D1D1`
- Texto: `#1F1F1F`
- Border-radius: `0.75rem`
- Padding: `1rem 1.5rem`
- Fuente: Georgia, 1.2rem
- Sin borde por defecto

### Tarjeta / Panel

- Fondo: `#2F2F2F`
- Border-radius: `1.25rem`
- Padding: `1.5rem`

### Elemento de lista

- Padding: `1rem 0.5rem`
- Borde inferior: `1px solid #3F3F3F`
- Layout: flex, espacio entre nombre y acciones

### Tablero de ajedrez

- Grid 8x8
- Tamano: `74vmin` en escritorio, `min(87vmin, calc(100vh - 150px))` en movil
- Casilla clara: `#D9C3A0`
- Casilla oscura: `#2B2B2B`

### Modal

- Fondo semitransparente con blur: `rgba(0,0,0,0.65)` + `backdrop-filter: blur(4px)`
- Caja interior: fondo `#1e1e1e`, borde `3px solid`
- Verde (`#22c55e`) para resultado positivo
- Rojo (`#ef4444`) para derrota
- Animacion de entrada con escala y desvanecimiento

### Cabecera

- Layout: flex, centrado
- Logo a la izquierda/centro
- Alias del jugador posicionado a la derecha en azul `#5A9FD4`

---

## 4. Definicion de formularios, controles e iconografia

### Formulario de login

| Campo | Tipo | Etiqueta | Placeholder | Restriccion |
|---|---|---|---|---|
| alias | text | Alias de jugador | Ingresa el alias ... | Max 8 caracteres, obligatorio |

### Controles del lobby

| Control | Tipo | Accion |
|---|---|---|
| Invitar | Boton verde | Envia solicitud de partida al jugador seleccionado |
| Aceptar (✓) | Boton icono verde circular | Acepta una invitacion de partida |
| Rechazar (✕) | Boton icono rojo circular | Rechaza una invitacion de partida |

### Controles del juego

| Control | Tipo | Accion |
|---|---|---|
| Tablas | Boton verde | Ofrece tablas al rival |
| Rendirse | Boton rojo | El jugador local concede la victoria al rival |
| Volver | Boton verde (modal) | Regresa al lobby tras finalizar la partida |
| Aceptar tablas | Boton verde (modal) | Acepta la oferta de tablas del rival |
| Rechazar tablas | Boton rojo (modal) | Rechaza la oferta de tablas del rival |

### Iconografia

| Icono | Caracter | Uso |
|---|---|---|
| Aceptar | ✓ | Boton de aceptar invitacion en el lobby |
| Rechazar | ✕ | Boton de rechazar invitacion en el lobby |
| Advertencia | ⚠️ | Aviso de movimiento invalido en el tablero |
| Piezas | PNG | Imagenes de las piezas de ajedrez (blancas y negras) |

### Piezas del ajedrez

Las piezas se renderizan como imagenes PNG en la carpeta `img/piezas/`:

- `blancas_rey.png`, `blancas_reina.png`, `blancas_torre.png`
- `blancas_alfil.png`, `blancas_caballo.png`, `blancas_peon.png`
- `negras_rey.png`, `negras_reina.png`, `negras_torre.png`
- `negras_alfil.png`, `negras_caballo.png`, `negras_peon.png`

---

## 5. Definicion de estados

### Botones

| Estado | Boton verde | Boton rojo |
|---|---|---|
| Normal | `#23CF46` | `#CF3232` |
| Hover | `#1EB93D` (transicion 0.3s) | `#B02A2A` (transicion 0.3s) |
| Deshabilitado | Opacidad reducida, sin cursor | Opacidad reducida, sin cursor |

### Campo de texto (input alias)

| Estado | Apariencia |
|---|---|
| Normal | Fondo `#D1D1D1`, sin borde visible |
| Focus | Outline `2px solid #23CF46` |
| Error | Mensaje de error visible en rojo `#CF3232` debajo del campo |

### Casillas del tablero

| Estado | Color |
|---|---|
| Clara (normal) | `#D9C3A0` |
| Oscura (normal) | `#2B2B2B` |
| Seleccionada | `rgba(35, 207, 70, 0.55)` (verde semitransparente) |
| Rey en jaque | `rgba(220, 38, 38, 0.55)` (rojo semitransparente) con animacion de parpadeo |

### Nombre del jugador en partida

| Estado | Apariencia |
|---|---|
| Turno inactivo | Borde transparente |
| Turno activo | Borde `2px solid #5A9FD4` (azul) |

### Aviso de movimiento invalido

| Estado | Apariencia |
|---|---|
| Oculto | `display: none` |
| Visible | Aparece sobre el tablero con animacion de entrada, fondo `rgba(220,38,38,0.92)`, desaparece a los 2 segundos |

### Modal de fin de partida

| Estado | Borde de la caja |
|---|---|
| Victoria o empate | Verde `#22c55e` con sombra verde |
| Derrota | Rojo `#ef4444` con sombra roja |
