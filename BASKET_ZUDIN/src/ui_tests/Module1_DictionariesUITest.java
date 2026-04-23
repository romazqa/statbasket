package ui_tests;

import com.MainWindow;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.*;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.DialogMatcher;
import org.junit.*;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.Allure;

@Epic("UI Тестирование десктопного клиента")
@Feature("Модуль 1: Справочники")
public class Module1_DictionariesUITest {
    private FrameFixture window;

    @Before
    public void setUp() {
        MainWindow frame = GuiActionRunner.execute(() -> new MainWindow());
        window = new FrameFixture(frame);
        window.show();
    }

    @Test
    @Story("Создание команды и привязка к ней игрока (CR-1.1)")
    public void testCreateTeamAndPlayer() {
        Allure.step("Открытие справочника 'Команды' и диалога добавления", () -> {
            window.button(JButtonMatcher.withText(".*КОМАНДЫ.*")).click();
            DialogFixture teamsDialog = window.dialog(DialogMatcher.withTitle("Команды"));
            sleep(1000);
            teamsDialog.button(JButtonMatcher.withText("Добавить")).click();
        });

        Allure.step("Заполнение данных новой команды и сохранение", () -> {
            DialogFixture addTeamDialog = window.dialog(DialogMatcher.withTitle("Добавление новой команды"));
            addTeamDialog.textBox("tfTeamName").setText("QA Тест Команда");
            addTeamDialog.textBox("tfCity").setText("Москва");
            addTeamDialog.textBox("tfGender").setText("М");
            addTeamDialog.button(JButtonMatcher.withText("OK")).click();
            sleep(1000);
            window.dialog(DialogMatcher.withTitle("Команды")).button(JButtonMatcher.withText("Закрыть")).click();
        });

        Allure.step("Открытие справочника 'Игроки' и диалога добавления", () -> {
            window.button(JButtonMatcher.withText(".*ИГРОКИ.*")).click();
            DialogFixture playersDialog = window.dialog(DialogMatcher.withTitle("Игроки"));
            sleep(1000);
            playersDialog.button(JButtonMatcher.withText("Добавить")).click();
        });

        Allure.step("Заполнение данных игрока, выбор команды и сохранение", () -> {
            DialogFixture addPlayerDialog = window.dialog(DialogMatcher.withTitle("Добавление нового игрока"));
            addPlayerDialog.textBox("tfPlayerName").setText("Автоматизатор Иван");
            addPlayerDialog.textBox("tfBirthday").setText("15-05-2000");
            addPlayerDialog.textBox("tfHeight").setText("205");
            addPlayerDialog.textBox("tfWeight").setText("105");
            addPlayerDialog.textBox("tfRole").setText("Центровой");
            addPlayerDialog.textBox("tfGameNumber").setText("99");
            addPlayerDialog.textBox("tfGender").setText("М");
            addPlayerDialog.comboBox("cbTeam").selectItem(0);
            addPlayerDialog.button(JButtonMatcher.withText("OK")).click();
            
            // Если появляется сообщение об успехе, закрываем его
            try { addPlayerDialog.optionPane().okButton().click(); } catch (Exception e) {}
            sleep(1000);
        });

        Allure.step("Проверка успешного сохранения игрока в таблице", () -> {
            DialogFixture playersDialog = window.dialog(DialogMatcher.withTitle("Игроки"));
            playersDialog.table().cell("Автоматизатор Иван").click();
            playersDialog.button(JButtonMatcher.withText("Закрыть")).click();
        });
    }

    @Test
    @Story("Валидация типов данных при создании соревнования (REG-1.1)")
    public void testEventYearValidationFails() {
        Allure.step("Открытие диалога добавления соревнования", () -> {
            window.menuItemWithPath("Справочники", "Соревнования").click();
            DialogFixture eventDialog = window.dialog(DialogMatcher.withTitle("Соревнования"));
            sleep(1000);
            eventDialog.button(JButtonMatcher.withText("Добавить")).click();
        });

        Allure.step("Ввод некорректных данных (буквы в поле года) и сохранение", () -> {
            DialogFixture addEventDialog = window.dialog(DialogMatcher.withTitle("Добавление нового соревнования"));
            addEventDialog.textBox("tfEventName").setText("Кубок UI Тестов");
            addEventDialog.textBox("tfLocation").setText("Москва");
            addEventDialog.textBox("tfYear").setText("Две тысячи"); // Вводим буквы вместо цифр
            addEventDialog.comboBox("cbLevel").selectItem(0);
            addEventDialog.button(JButtonMatcher.withText("OK")).click();
        });

        Allure.step("Ожидание окна с ошибкой валидации", () -> {
            DialogFixture addEventDialog = window.dialog(DialogMatcher.withTitle("Добавление нового соревнования"));
            JOptionPaneFixture errorDialog = addEventDialog.optionPane();
            errorDialog.requireErrorMessage(); 
            errorDialog.okButton().click();    
            
            // Закрываем окна
            addEventDialog.button(JButtonMatcher.withText("Отмена")).click(); 
            window.dialog(DialogMatcher.withTitle("Соревнования")).button(JButtonMatcher.withText("Закрыть")).click();
        });
    }

    @After
    public void tearDown() {
        if (window != null) window.cleanUp();
    }

    private void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {}
    }
}