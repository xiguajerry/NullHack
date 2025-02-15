package love.xiguajerry.nullhack.config

/**
 * Structure:
 * ```
 * Categories
 *    ├─►namespace:default
 *    │     ├─►NamespacedConfigurationManager(name1)
 *    │     │     ├─►NamedConfigurables
 *    │     │     │     ├─►NamedConfigurable1
 *    │     │     │     ├─►NamedConfigurable2
 *    │     │     │     └─►...
 *    │     │     └─►AnonymousConfigurables
 *    │     │           ├─►AnonymousConfigurable1
 *    │     │           ├─►AnonymousConfigurable2
 *    │     │           └─►...
 *    │     ├─►NamespacedConfigurationManager(name2)
 *    │     │     ├─►NamedConfigurables
 *    │     │     │     ├─►NamedConfigurable1
 *    │     │     │     ├─►NamedConfigurable2
 *    │     │     │     └─►...
 *    │     │     └─►AnonymousConfigurables
 *    │     │           ├─►AnonymousConfigurable1
 *    │     │           ├─►AnonymousConfigurable2
 *    │     │           └─►...
 *    │     └─►...
 *    │─►namespace:preset1
 *    │     ├─►NamespacedConfigurationManager(name1)
 *    │     │     ├─►NamedConfigurables
 *    │     │     │     ├─►NamedConfigurable1
 *    │     │     │     ├─►NamedConfigurable2
 *    │     │     │     └─►...
 *    │     │     └─►AnonymousConfigurables
 *    │     │           ├─►AnonymousConfigurable1
 *    │     │           ├─►AnonymousConfigurable2
 *    │     │           └─►...
 *    │     ├─►NamespacedConfigurationManager(name2)
 *    │     │     ├─►NamedConfigurables
 *    │     │     │     ├─►NamedConfigurable1
 *    │     │     │     ├─►NamedConfigurable2
 *    │     │     │     └─►...
 *    │     │     └─►AnonymousConfigurables
 *    │     │           ├─►AnonymousConfigurable1
 *    │     │           ├─►AnonymousConfigurable2
 *    │     │           └─►...
 *    │     └─►...
 *    └─►...
 * ```
 */
class Categories {
    private val categoryMap = HashMap<String, NamespacedConfigurationManager>()

    fun getConfigurationManager(category: String) = categoryMap.computeIfAbsent(category) {
        NamespacedConfigurationManager(it)
    }

    fun clean() {
        categoryMap.values.forEach(NamespacedConfigurationManager::clean)
    }

    fun read() {
        categoryMap.values.forEach(NamespacedConfigurationManager::read)
    }

    fun save() {
        categoryMap.values.forEach(NamespacedConfigurationManager::save)
    }
}