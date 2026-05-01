package ru.netology.delivery.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

class DeliveryTest {

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);
        // TODO: добавить логику теста в рамках которого будет выполнено планирование и перепланирование встречи.
        // Для заполнения полей формы можно использовать пользователя validUser и строки с датами в переменных
        // firstMeetingDate и secondMeetingDate. Можно также вызывать методы generateCity(locale),
        // generateName(locale), generatePhone(locale) для генерации и получения в тесте соответственно города,
        // имени и номера телефона без создания пользователя в методе generateUser(String locale) в датагенераторе
        // Уменьшаем зону поиска - поиск по форме на странице
        SelenideElement formElement = $("form");

        // Поиск поля города и задание города пользователя
        formElement.$("[data-test-id='city'] input").setValue(validUser.getCity());

        // Поиск поля даты и задание первой встречи
        formElement.$("[data-test-id='date'] input.input__control").press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        formElement.$("[data-test-id='date'] input.input__control").setValue(firstMeetingDate);

        // Поиск поля имени и задание имени пользователя
        formElement.$("[data-test-id='name'] input").setValue(validUser.getName());

        // Поиск поля номера телефона и задание телефона пользователя
        formElement.$("[data-test-id='phone'] input").setValue(validUser.getPhone());

        // Поиск флажка согласия и нажатие
        formElement.$("[data-test-id='agreement']").click();

        // Поиск кнопки Запланировать и нажатие на нее
        formElement.$(".button").click();

        // Поиск информационного блока с записью и сверка успешности
        $("[data-test-id='success-notification'] .notification__content").should(Condition.visible)
                .should(Condition.text("Встреча успешно запланирована на " + firstMeetingDate));

        // Поиск поля даты и задание второй встречи
        formElement.$("[data-test-id='date'] input.input__control").press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        formElement.$("[data-test-id='date'] input.input__control").setValue(secondMeetingDate);

        // Поиск кнопки Запланировать и нажатие на нее
        formElement.$(".button").click();

        // Поиск информационного блока
        $("[data-test-id='replan-notification'] .notification__content").should(Condition.visible, Duration.ofSeconds(15))
                .should(Condition.text("У вас уже запланирована встреча на другую дату. Перепланировать?"));
        // Поиск кнопки Перепланировать и нажатие на нее
        $("[data-test-id='replan-notification'] .button").click();

        // Поиск информационного блока с записью и сверка успешности
        $("[data-test-id='success-notification'] .notification__content").should(Condition.visible)
                .should(Condition.text("Встреча успешно запланирована на " + secondMeetingDate));
    }
}
