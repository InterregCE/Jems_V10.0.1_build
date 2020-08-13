package io.cloudflight.ems.nuts.service

import io.cloudflight.ems.nuts.entity.NutsCountry
import io.cloudflight.ems.nuts.entity.NutsRegion1
import io.cloudflight.ems.nuts.entity.NutsRegion2
import io.cloudflight.ems.nuts.entity.NutsRegion3
import org.springframework.http.client.ClientHttpResponse
import org.springframework.util.StreamUtils
import org.springframework.web.client.ResponseExtractor
import java.io.File
import java.io.FileOutputStream

private val CSV_DELIMITER = Regex(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*\$)")
private val TRAILING_QUOTES = Regex("^\"+|\"+$")

const val COUNTRY_CODE_LENGTH = 2
const val REGION_1_CODE_LENGTH = 3
const val REGION_2_CODE_LENGTH = 4
const val REGION_3_CODE_LENGTH = 5

const val GISCO_NUTS_URL = "https://gisco-services.ec.europa.eu/distribution/v2/nuts"
const val GISCO_DATASETS_FILE = "datasets.json"
const val NUTS_ID = "NUTS_ID"
const val NUTS_NAME = "NUTS_NAME"

fun String.removeWrappingQuotes() = replace(TRAILING_QUOTES, "")

fun String.splitCsvLine() = split(CSV_DELIMITER, 0).map { it.removeWrappingQuotes() }

fun groupNuts(
    nuts: Iterable<NutsRegion3>
): Map<
    NutsIdentifier, // Country
    Map<
        NutsIdentifier, // NUTS_1
        Map<
            NutsIdentifier, // NUTS_2
            Set<NutsIdentifier>>>> {
    val result: MutableMap<NutsIdentifier, MutableMap<NutsIdentifier, MutableMap<NutsIdentifier, MutableSet<NutsIdentifier>>>> = HashMap()

    nuts.forEach {
        val countryId = it.region2.region1.country.toOutput()
        val nuts1Id = it.region2.region1.toOutput()
        val nuts2Id = it.region2.toOutput()
        val nuts3Id = it.toOutput()
        result.getOrPut(countryId) { HashMap() }
            .getOrPut(nuts1Id) { HashMap() }
            .getOrPut(nuts2Id) { HashSet() }
            .add(nuts3Id)
    }
    return result
}

/**
 * This data class is optimized to be used as a key in hash collections (HashMap or HashSet).
 */
data class NutsIdentifier(
    val id: String,
    val title: String
) {

    override fun equals(other: Any?): Boolean = (other is NutsIdentifier) && id == other.id

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return title
    }

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
