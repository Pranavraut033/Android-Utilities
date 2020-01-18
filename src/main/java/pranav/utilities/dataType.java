package pranav.utilities;

import androidx.annotation.StringDef;

@StringDef(value = {
        "BLOB",
        "BOOLEAN",
        "INT",
        "MEDIUMINT",
        "BIGINT",
        "FLOAT",
        "DOUBLE",
        "CHARACTER",
        "TEXT",
        "TIMESTAMP",
        "DATETIME"
})
@interface dataType {
}
