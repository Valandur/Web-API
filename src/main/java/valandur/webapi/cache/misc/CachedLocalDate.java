package valandur.webapi.cache.misc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.cache.CachedObject;

import java.time.LocalDate;

@ApiModel("LocalDate")
public class CachedLocalDate extends CachedObject<LocalDate> {

    private int day;
    @ApiModelProperty("The day of the month (1-31)")
    public int getDay() {
        return day;
    }

    private int month;
    @ApiModelProperty("The month in the year (1-12)")
    public int getMonth() {
        return month;
    }

    private int year;
    @ApiModelProperty("The year")
    public int getYear() {
        return year;
    }


    public CachedLocalDate(LocalDate value) {
        super(value);

        this.day = value.getDayOfMonth();
        this.month = value.getMonthValue();
        this.year = value.getYear();
    }
}
