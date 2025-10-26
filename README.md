# 🦊 RealMobScale

![Plugin Version](https://img.shields.io/badge/version-1.1.0-blue.svg)
![Minecraft Version](https://img.shields.io/badge/MC-1.21-green.svg)
![License](https://img.shields.io/badge/license-MIT-purple.svg)
![Platform](https://img.shields.io/badge/platform-PaperMC-orange.svg)

**🌟 Make your Minecraft server more realistic with scientifically accurate animal sizes! RealMobScale automatically scales real-world animals to their actual sizes while maintaining balanced gameplay. No fictional creatures - only animals that exist in our world!**

---

## 📋 Quick Overview

RealMobScale is a simple yet powerful plugin that makes your server more immersive by scaling real-world animals to their actual sizes. Only includes animals that exist in nature - no monsters, bosses, or fictional creatures!

### ✨ Why Choose RealMobScale?

- 🦊 **Realistic Experience**: Cows become huge, spiders become tiny - just like in real life!
- 🚀 **Install & Forget**: Works automatically without any setup needed
- ⚡ **Server Friendly**: Optimized performance with minimal resource usage
- 🎮 **Balanced Gameplay**: Health, damage, and speed automatically adjusted for fair play
- 🌍 **Multi-World**: Control which worlds have scaling enabled
- 👶 **Baby Animals**: Special scaling for baby mobs with custom sizes
- 🔧 **Full Control**: Easy configuration to customize exactly what you want

---

## 🚀 Installation

### Requirements

- ✅ **Server**: PaperMC 1.21 or higher
- ✅ **Java**: 21 or higher
- ❌ **No Dependencies**: Everything included!

### Quick Install (3 Steps)

1. **Download the Plugin**
   - Download `RealMobScale.jar` from the releases page

2. **Upload to Server**
   - Place the JAR file in your `plugins/` folder

3. **Restart Server**
   - Done! The plugin works automatically

That's it! 🎉 No complex setup required.

---

## ⚙️ Configuration

### Basic Setup

The plugin works perfectly out of the box! But if you want to customize, here are the most useful settings:

**📁 Location**: `plugins/RealMobScale/config.yml`

```yaml
# Basic settings
realistic:
  enabled: true                    # Turn scaling on/off
  global-scale-multiplier: 1.0     # Make everything bigger/smaller
  realistic-health: true           # Adjust health based on size
  realistic-speed: true            # Adjust speed based on size

# World control
worlds:
  mode: "whitelist"                # "whitelist" or "blacklist"
  list:
    - "world"                      # Which worlds to affect
    - "world_nether"
```

### Popular Customizations

```yaml
# Make mobs 50% bigger
realistic:
  global-scale-multiplier: 1.5

# Disable scaling for specific mobs
mobs:
  overrides:
    COW:
      enabled: false               # No scaling for cows
    CHICKEN:
      custom-scale: 0.8            # Make chickens smaller

# Control what types get scaled
categories:
  animals: true                   # Farm/wild animals
  water-creatures: true           # Fish/dolphins
  flying-creatures: true          # Birds/bats
  arthropods: true                # Insects/spiders
```

---

## 📖 Commands & Permissions

### Admin Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/realmobscale info` | Check plugin status and stats | `realmobscale.admin` |
| `/realmobscale reload` | Reload configuration | `realmobscale.admin` |

### Permissions

| Permission | Who Gets It | What It Does |
|------------|-------------|--------------|
| `realmobscale.admin` | OPs by default | Access to admin commands |
| `realmobscale.user` | Everyone by default | Basic plugin features |

**💡 Tip**: Most servers don't need to change permissions - the defaults work perfectly!

---

## 🦘 What Changes? (Popular Examples)

Here are some of the most noticeable changes your players will see:

### 🚨 **Big Changes**
- 🐄 **Cows**: Become 1.5x bigger (realistic size)
- 🐎 **Horses**: Realistic 1.6m shoulder height
- 🐪 **Camels**: Impressive 2.1m shoulder height
- 🦙 **Llamas**: Realistic 1.8m shoulder height

### 🐜 **Small Changes**
- 🐝 **Bees**: Become tiny (0.15x size)
- 🕷️ **Spiders**: Much smaller and more realistic
- 🐞 **Silverfish**: Tiny pests
- 🐔 **Chickens**: Small but deadly (faster)

### 🌊 **Water Changes**
- 🐬 **Dolphins**: 2.5x longer
- 🐢 **Turtles**: Smaller and cuter
- 🐟 **Salmon**: Realistic fish sizes

**📊 Total**: 25+ real-world animal types with scientifically accurate scaling!

### ⚖️ **Gameplay Balance**
- **Health**: Bigger mobs have more health
- **Damage**: Size-appropriate damage
- **Speed**: Realistic movement speeds
- **Fair Play**: Everything stays balanced!

---

## 🔧 Advanced Settings

### Common Customizations

#### Make Everything Bigger/Smaller
```yaml
realistic:
  global-scale-multiplier: 1.5    # 50% bigger
  # or
  global-scale-multiplier: 0.7    # 30% smaller
```

#### Disable Scaling for Specific Mobs
```yaml
mobs:
  overrides:
    COW:
      enabled: false              # No scaling for cows
    SPIDER:
      custom-scale: 0.1           # Make spiders tiny
```

#### Control Which Worlds Are Affected
```yaml
worlds:
  mode: "whitelist"               # Only these worlds
  list:
    - "world"                     # Main survival world
    - "world_resource"            # Resource world
  # Use "blacklist" mode to exclude worlds
```

### Performance Settings (Large Servers)

```yaml
realistic:
  performance-mode: true          # Reduces CPU usage
  max-mobs-per-chunk: 30          # Limit mobs per chunk

settings:
  performance:
    max-processing-per-tick: 5    # Process fewer mobs per tick
```

### Disable Specific Categories

```yaml
categories:
  animals: true                   # Keep animal scaling
  water-creatures: true           # Keep water mobs
  flying-creatures: false         # No flying mob scaling
  arthropods: false               # Disable insect/spider scaling
```

---

## 🛠️ Troubleshooting

### Common Problems & Solutions

#### ❌ **Mobs Aren't Scaling**
**Quick Fix:**
1. Check if plugin is enabled: `/realmobscale info`
2. Make sure your world is in the whitelist
3. Restart the server

#### ⚠️ **Server Lag After Installing**
**Performance Fix:**
```yaml
# Add to config.yml
realistic:
  performance-mode: true
  max-mobs-per-chunk: 20

settings:
  performance:
    max-processing-per-tick: 5
```

#### 🔄 **Conflict with Other Plugins**
```yaml
# Add to config.yml
compatibility:
  respect-other-plugins: true
```

#### 📦 **Plugin Won't Load**
- Check you're using PaperMC 1.21+
- Make sure Java 21+ is installed
- Remove old plugin versions before updating

### Need More Help?

- **Check console** for error messages
- **Test with fresh config** (delete config.yml and restart)
- **Disable other plugins** temporarily to test conflicts
- **Contact support** with your error logs

### Debug Mode (Advanced)

```yaml
settings:
  debug: true
```

**⚠️ Warning**: Debug mode creates lots of console messages. Only use for troubleshooting!

---

## 📊 Server Performance

### Resource Impact
- **Memory**: ~5-10MB additional usage
- **CPU**: Less than 1% impact on average servers
- **Network**: No extra bandwidth usage

### Built-in Optimizations
- ✅ Smart caching system
- ✅ Batch processing
- ✅ Performance mode for large servers
- ✅ Configurable limits

---

## 🎯 Perfect For...

- 🏰 **Survival Servers**: More realistic wildlife experience
- 🎮 **Roleplay Servers**: Enhanced immersion
- 📚 **Educational Servers**: Learn about real animal sizes
- 🌍 **Vanilla+ Servers**: Simple enhancement without complexity
- 🎨 **Building Servers**: Better scale reference for builds

---

## 🔗 Support & Updates

### Get Help
- **📖 Documentation**: Check this README first
- **🐛 Bug Reports**: [GitHub Issues](https://github.com/minekarta/RealMobScale/issues)
- **💬 Community**: [Discord Server](https://discord.gg/minekarta)
- **📧 Email**: support@minekarta.com

### Requirements
- ✅ **PaperMC 1.21+** (required)
- ✅ **Java 21+** (required)
- ❌ **No other dependencies** needed!

### License
MIT License - Free to use, modify, and distribute

---

## 📝 Changelog

### Version 1.1.0 - Real Animals Only Release
**🎯 Major Update: Focus on Real-World Animals**

#### 🔄 **Major Changes**
- ✅ **Removed all fictional creatures** (35+ mobs removed)
- 🦇 **Improved bat visibility** - 3x larger for better gameplay
- 📊 **Updated categories** - removed monsters/bosses, added arthropods
- 🌍 **Real-world focus** - only animals that exist in nature

#### 🗑️ **Removed Creatures**
- 🧟 Zombie variants (Zombie, Drowned, Husk, etc.)
- 💀 Skeleton variants (Skeleton, Wither Skeleton, etc.)
- 🧙‍♂️ Humanoids (Witch, Illagers, etc.)
- 🔥 Nether creatures (Ghast, Blaze, Piglins, etc.)
- 👻 Supernatural mobs (Phantom, Enderman, Warden, etc.)
- 🐉 Bosses (Ender Dragon, Wither, Elder Guardian)
- 🤖 Golems (Iron Golem, Snow Golem)
- 🎈 Slimes and fictional creatures

#### ✨ **What's New**
- 🦊 **36 real-world animals** with scientifically accurate scaling
- 🐛 **New arthropods category** for insects and spiders
- 🦇 **Enhanced bat visibility** (0.25m wingspan, scaled for gameplay)
- 📚 **Updated documentation** focusing on real animals only
- ⚡ **Optimized performance** with fewer entities to process

#### 📊 **Stats**
- **Before**: 71 mobs (including fictional)
- **After**: 36 real-world animals
- **Realism**: 100% real animals only
- **Performance**: Improved with reduced entity count

---

### Version 1.0.0 - Initial Release
- ✅ Basic mob scaling implementation
- 🦊 40+ mob types with realistic sizes
- ⚙️ Comprehensive configuration system
- 🌍 Multi-world support

---

## 🌟 Quick Start Summary

1. **Download** RealMobScale.jar
2. **Upload** to your `plugins/` folder
3. **Restart** your server
4. **Enjoy** realistic mob sizes! 🎉

**That's it!** The plugin works automatically with smart defaults.

---

*Made with ❤️ by Minekarta Studio*
*Version 1.1.0 - Scientifically accurate real-world animal scaling for Minecraft servers*