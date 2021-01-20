package io.cloudflight.jems.server.nuts.service

import io.cloudflight.jems.api.nuts.dto.OutputNuts
import io.cloudflight.jems.server.nuts.entity.NutsCountry
import io.cloudflight.jems.server.nuts.entity.NutsRegion1
import io.cloudflight.jems.server.nuts.entity.NutsRegion2
import io.cloudflight.jems.server.nuts.entity.NutsRegion3
import org.springframework.http.client.ClientHttpResponse
import org.springframework.util.StreamUtils
import org.springframework.web.client.ResponseExtractor
import java.io.File
import java.io.FileOutputStream
import java.text.Collator
import java.util.Locale
import java.util.TreeMap
import java.util.TreeSet

private val LINE_BREAK_REGEX = Regex("[\\n\\r]+")

const val COUNTRY_CODE_LENGTH = 2
const val REGION_1_CODE_LENGTH = 3
const val REGION_2_CODE_LENGTH = 4
const val REGION_3_CODE_LENGTH = 5

const val GISCO_NUTS_URL = "https://gisco-services.ec.europa.eu/distribution/v2/nuts"
const val GISCO_DATASETS_FILE = "datasets.json"
const val NUTS_ID = "NUTS_ID"
const val NUTS_NAME = "NAME_LATN"

private val collatorGerman = Collator.getInstance(Locale.GERMAN)

fun String.removeLineBreaks() = replace(LINE_BREAK_REGEX, "")

fun groupNuts(
    nuts: Iterable<NutsRegion3>
): Map<
    NutsIdentifier, // Country
    Map<
        NutsIdentifier, // NUTS_1
        Map<
            NutsIdentifier, // NUTS_2
            Set<NutsIdentifier>>>> {
    val result: MutableMap<NutsIdentifier, MutableMap<NutsIdentifier, MutableMap<NutsIdentifier, MutableSet<NutsIdentifier>>>> = TreeMap()

    nuts.forEach {
        val countryId = it.region2.region1.country.toOutput()
        val nuts1Id = it.region2.region1.toOutput()
        val nuts2Id = it.region2.toOutput()
        val nuts3Id = it.toOutput()
        result.getOrPut(countryId) { TreeMap() }
            .getOrPut(nuts1Id) { TreeMap() }
            .getOrPut(nuts2Id) { TreeSet() }
            .add(nuts3Id)
    }
    return result
}

fun Map<NutsIdentifier, Map<NutsIdentifier, Map<NutsIdentifier, Set<NutsIdentifier>>>>.toOutputNuts() = map {
    OutputNuts(
        code = it.key.id,
        title = it.key.title,
        areas = it.value.map {
            OutputNuts(
                code = it.key.id,
                title = it.key.title,
                areas = it.value.map {
                    OutputNuts(
                        code = it.key.id,
                        title = it.key.title,
                        areas = it.value.map {
                            OutputNuts(
                                code = it.id,
                                title = it.title
                            )
                        }
                    )
                }
            )
        }
    )
}

/**
 * This data class is optimized to be used as a key in hash collections (HashMap or HashSet).
 */
data class NutsIdentifier(
    val id: String,
    val title: String
): Comparable<NutsIdentifier> {

    override fun equals(other: Any?): Boolean = (other is NutsIdentifier) && id == other.id

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "$id|$title"
    }

    override fun compareTo(other: NutsIdentifier): Int = collatorGerman.compare(this.title, other.title)

}

fun Pair<String, String>.toNutsCountry() = NutsCountry(
    id = first,
    title = second
)

fun Pair<String, String>.toNutsRegion1() = NutsRegion1(
    id = first,
    title = second,
    country = NutsCountry(id = first.substring(0, COUNTRY_CODE_LENGTH), title = "")
)

fun Pair<String, String>.toNutsRegion2() = NutsRegion2(
    id = first,
    title = second,
    region1 = NutsRegion1(
        id = first.substring(0, REGION_1_CODE_LENGTH),
        title = "",
        country = NutsCountry(id = first.substring(0, COUNTRY_CODE_LENGTH), title = "")
    )
)

fun Pair<String, String>.toNutsRegion3() = NutsRegion3(
    id = first,
    title = second,
    region2 = NutsRegion2(
        id = first.substring(0, REGION_2_CODE_LENGTH),
        title = "",
        region1 = NutsRegion1(
            id = first.substring(0, REGION_1_CODE_LENGTH),
            title = "",
            country = NutsCountry(id = first.substring(0, COUNTRY_CODE_LENGTH), title = "")
        )
    )
)

val csvFileExtractor: ResponseExtractor<File> = ResponseExtractor { response: ClientHttpResponse ->
    val file = File.createTempFile("nuts_tmp", ".csv")
    StreamUtils.copy(response.body, FileOutputStream(file))
    return@ResponseExtractor file
}
