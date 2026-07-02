# BadOmenTrade 🔮

Plugin para **Minecraft 1.21.1 (Paper / forks de Paper como Purpur)** que añade
una probabilidad configurable de recibir el efecto **Mal Presagio (Bad Omen)**
cada vez que un jugador completa un comercio con un aldeano.

> ⚠️ Requiere **Paper** (o un fork basado en Paper). No funciona en Spigot puro
> porque usa el evento `PlayerTradeEvent`, exclusivo de la API de Paper.

## ✨ Características

- Probabilidad configurable (por defecto **5%**) de recibir Mal Presagio al comerciar.
- Nivel y duración del efecto totalmente configurables (o casi permanente con `-1`).
- Mensajes de chat, **título/subtítulo** y **action bar** personalizables con colores (`&`).
- Sonido y partículas al activarse el efecto (configurables).
- Opción de incluir también al Mercader Errante (Wandering Trader).
- Lista de mundos donde el plugin queda deshabilitado.
- Aviso opcional a otros jugadores cuando alguien "cae maldecido".
- Comando `/badomentrade` con `reload`, `chance [valor]` e `info`, con tab-complete.
- Sistema de permisos (`badomentrade.admin`, `badomentrade.bypass`, `badomentrade.notify`).

## 📁 Estructura del proyecto

```
BadOmenTrade/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/badomentrade/plugin/
    │   ├── BadOmenTrade.java          (clase principal)
    │   ├── commands/BadOmenCommand.java
    │   └── listeners/TradeListener.java
    └── resources/
        ├── plugin.yml
        └── config.yml
```

## 🔨 Cómo compilar

### Opción A — Sin instalar nada (GitHub Actions)

El proyecto ya incluye `.github/workflows/build.yml`. Solo tienes que:

1. Crea un repositorio nuevo en GitHub y sube el contenido de esta carpeta
   (puedes arrastrar y soltar los archivos desde la web de GitHub, sin usar
   la terminal).
2. Ve a la pestaña **Actions** del repo: el workflow "Build BadOmenTrade"
   se ejecuta solo y compila el plugin en la nube (~1 minuto).
3. Entra a esa ejecución y descarga el artefacto **BadOmenTrade-jar** (es un
   .zip que contiene el `.jar` final).
4. Copia el `.jar` a la carpeta `plugins/` de tu servidor.

### Opción B — En tu máquina

Necesitas **JDK 21** y **Maven** instalados:

```bash
cd BadOmenTrade
mvn clean package
```

El `.jar` final aparecerá en `target/BadOmenTrade-1.0.0.jar`.

## 📥 Instalación

1. Copia `BadOmenTrade-1.0.0.jar` a la carpeta `plugins/` de tu servidor Paper 1.21.1.
2. Reinicia el servidor (se generará `plugins/BadOmenTrade/config.yml`).
3. Edita `config.yml` a tu gusto y usa `/badomentrade reload` para aplicar cambios sin reiniciar.

## ⚙️ Configuración principal (`config.yml`)

| Clave | Descripción | Valor por defecto |
|---|---|---|
| `chance` | Probabilidad (%) de recibir Mal Presagio al comerciar | `5.0` |
| `include-wandering-trader` | Si el Mercader Errante también cuenta | `false` |
| `amplifier` | Nivel del efecto (0 = Nivel I) | `0` |
| `duration-seconds` | Duración en segundos (`-1` = casi permanente) | `300` |
| `disabled-worlds` | Mundos donde el plugin no actúa | `[]` |
| `sound.*` | Sonido reproducido al activarse el efecto | `ENTITY_ILLUSIONER_CAST_SPELL` |
| `particles.*` | Partículas alrededor del jugador maldecido | `SMOKE` |
| `messages.*` | Mensajes de chat, título, subtítulo y action bar | — |

## 🎮 Comandos

| Comando | Permiso | Descripción |
|---|---|---|
| `/badomentrade reload` | `badomentrade.admin` | Recarga `config.yml` |
| `/badomentrade chance [valor]` | `badomentrade.admin` | Ve o cambia la probabilidad en caliente |
| `/badomentrade info` | `badomentrade.admin` | Muestra la configuración actual |

## 🔑 Permisos

| Permiso | Descripción | Por defecto |
|---|---|---|
| `badomentrade.admin` | Usa los comandos administrativos | `op` |
| `badomentrade.bypass` | El jugador nunca recibe el efecto | `false` |
| `badomentrade.notify` | Recibe avisos cuando otro jugador es maldecido | `op` |

## 💡 Notas

- El efecto se aplica usando el `PotionEffectType.BAD_OMEN` nativo de Minecraft, así
  que se comporta exactamente igual que el Mal Presagio obtenido al derrotar a un
  Capitán Illager (activa un raid al entrar a un pueblo si el nivel del efecto es suficiente).
- Todos los sonidos y partículas usan los `enum` de Bukkit (`org.bukkit.Sound` y
  `org.bukkit.Particle`); si escribes un nombre inválido en el config, el plugin
  avisará en consola sin romperse.
