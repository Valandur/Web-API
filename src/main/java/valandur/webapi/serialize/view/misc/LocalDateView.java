package valandur.webapi.serialize.view.misc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.serialize.BaseView;

import java.time.LocalDate;

@ApiModel("LocalDate")
public class LocalDateView extends BaseView<LocalDate> {

    @ApiModelProperty("The day of the month (1-31)")
    public int getDay() {
        return value.getDayOfMonth();
    }

    @ApiModelProperty("The month in the year (1-12)")
    public int getMonth() {
        return value.getMonthValue();
    }

    @ApiModelProperty("The year")
    public int getYear() {
        return value.getYear();
    }


    public LocalDateView(LocalDate value) {
        super(value);
    }
}
