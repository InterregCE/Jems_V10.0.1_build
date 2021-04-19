package jems.plugin

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = ["org.example"])
open class PluginConfig {
}
