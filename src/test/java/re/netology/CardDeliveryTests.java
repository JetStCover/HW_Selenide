package re.netology;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;
import static jdk.internal.misc.ThreadFlock.open;

public class CardDeliveryTests {

    String calcDate(int days, String format){
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern(format, Locale.forLanguageTag("ru")));
    }

    @BeforeEach
    void prepareForTest() {
        open("http://localhost:9999/");
    }


    @Test
    void positiveCardDeliveryTest() {
        String date = calcDate(3, "dd.MM.yyyy");

        $("[data-test-id='city'] input").setValue("Москва");
        $("[data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(date);
        $("[data-test-id='name'] input").setValue("Мария Иванова");
        $("[data-test-id='phone'] input").setValue("+79999999999");
        $("[data-test-id='agreement']").click();
        $(".button").click();
        $("[data-test-id='notification'] .notification__content").shouldBe(visible, Duration.ofSeconds(15)).shouldHave(text("Встреча успешно забронирована на " + date));
    }

    @Test
    void findByTwoLettersInCityTest() {
        String city = "Москва";

        $("[data-test-id='city'] input").setValue("Мо");
        $$(".menu-item>.menu-item__control").find(Condition.text(city)).click();
        $("[data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(calcDate(3, "dd MM yyyy"));
        $("[data-test-id='name'] input").setValue("Мария Иванова");
        $("[data-test-id='phone'] input").setValue("+79999999999");
        $("[data-test-id='agreement']").click();
        $(".button.button").click();
        Assertions.assertEquals(city, $("[data-test-id='city'] input").getValue());
        $(withText("Встреча успешно забронирована")).shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    void emptyFormTest() {
        $("button.button").click();
        $("[data-test-id='city'] .input__sub").shouldHave(Condition.text("Поле обязательно для заполнения"));
    }

    @Test
    void datePickerTest() {

        String monthYear = calcDate(7, "LLLL yyyy");
        String day = calcDate(7, "dd");
        String date = calcDate(7, "dd.MM.yyyy");

        $("[data-test-id='city'] input").setValue("Москва");
        $(".input__icon").click();
        String currentMonth = $(".calendar__name").text().toLowerCase();
        if (!currentMonth.equals(monthYear)) {
            $("[data-step='1']").click();
        }
        $$(".calendar__day").find(Condition.text(day)).click();
        $("[data-test-id='name'] input").setValue("Мария Иванова");
        $("[data-test-id='phone'] input").setValue("+79999999999");
        $("[data-test-id='agreement']").click();
        $(".button.button").click();
        $("[data-test-id='notification'] .notification__content").shouldBe(visible, Duration.ofSeconds(15)).shouldHave(text("Встреча успешно забронирована на " + date));
    }

    @Test
    void invalidCityTest() {

        $("[data-test-id='city'] input").setValue("Moscow");
        $("[data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(calcDate(3, "dd MM yyyy"));
        $("[data-test-id='name'] input").setValue("Мария Иванова");
        $("[data-test-id='phone'] input").setValue("+79999999999");
        $("[data-test-id='agreement']").click();
        $(".button.button").click();
        $("[data-test-id='city'] .input__sub").shouldHave(Condition.text("Доставка в выбранный город недоступна"));
    }

    @Test
    void invalidNameTest() {

        $("[data-test-id='city'] input").setValue("Москва");
        $("[data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(calcDate(3, "dd MM yyyy"));
        $("[data-test-id='name'] input").setValue("Maria");
        $("[data-test-id='phone'] input").setValue("+79999999999");
        $("[data-test-id='agreement']").click();
        $(".button.button").click();
        $("[data-test-id='name'] .input__sub").shouldHave(Condition.text("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test
    void invalidPhoneTest() {

        $("[data-test-id='city'] input").setValue("Москва");
        $("[data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(calcDate(3, "dd MM yyyy"));
        $("[data-test-id='name'] input").setValue("Мария Иванова");
        $("[data-test-id='phone'] input").setValue("+799999999");
        $("[data-test-id='agreement']").click();
        $(".button.button").click();
        $("[data-test-id='phone'] .input__sub").shouldHave(Condition.text("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));

    }

    @Test
    void emptyAgreementTest() {

        $("[data-test-id='city'] input").setValue("Москва");
        $("[data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(calcDate(3, "dd MM yyyy"));
        $("[data-test-id='name'] input").setValue("Мария Иванова");
        $("[data-test-id='phone'] input").setValue("+79999999999");
        $(".button.button").click();
        $("[data-test-id='agreement'].input_invalid").shouldBe(visible);
    }
}