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
            // ИСПРАВЛЕНИЕ: Добавили .andShowing()
            DialogFixture teamsDialog = window.dialog(DialogMatcher.withTitle("Команды").andShowing());
            sleep(1000);
            teamsDialog.button("btnAdd").click();
        });

        Allure.step("Заполнение данных новой команды и сохранение", () -> {
            DialogFixture addTeamDialog = window.dialog(DialogMatcher.withTitle("Добавление новой команды").andShowing());
            addTeamDialog.textBox("tfTeamName").setText("QA Тест Команда");
            addTeamDialog.textBox("tfCity").setText("Москва");
            addTeamDialog.textBox("tfGender").setText("М");
            addTeamDialog.button("btnOk").click();
            sleep(1000);
            window.dialog(DialogMatcher.withTitle("Команды").andShowing()).button("btnClose").click();
        });

        Allure.step("Открытие справочника 'Игроки' и диалога добавления", () -> {
            window.button(JButtonMatcher.withText(".*ИГРОКИ.*")).click();
            DialogFixture playersDialog = window.dialog(DialogMatcher.withTitle("Игроки").andShowing());
            sleep(1000);
            playersDialog.button("btnAdd").click();
        });

        Allure.step("Заполнение данных игрока, выбор команды и сохранение", () -> {
            DialogFixture addPlayerDialog = window.dialog(DialogMatcher.withTitle("Добавление нового игрока").andShowing());
            addPlayerDialog.textBox("tfPlayerName").setText("Автоматизатор Иван");
            addPlayerDialog.textBox("tfBirthday").setText("15-05-2000");
            addPlayerDialog.textBox("tfHeight").setText("205");
            addPlayerDialog.textBox("tfWeight").setText("105");
            addPlayerDialog.textBox("tfRole").setText("Центровой");
            addPlayerDialog.textBox("tfGameNumber").setText("99");
            addPlayerDialog.textBox("tfGender").setText("М");
            addPlayerDialog.comboBox("cbTeam").selectItem(0);
            addPlayerDialog.button("btnOk").click();
            
            try { addPlayerDialog.optionPane().okButton().click(); } catch (Exception e) {}
            sleep(1000);
        });

        Allure.step("Проверка успешного сохранения игрока в таблице", () -> {
            DialogFixture playersDialog = window.dialog(DialogMatcher.withTitle("Игроки").andShowing());
            playersDialog.table("tblPlayer").cell("Автоматизатор Иван").click();
            playersDialog.button("btnClose").click();
        });
    }

    @Test
    @Story("Создание уровня соревнований и самого соревнования (CR-1.1)")
    public void testCreateLevelAndEvent() {
        Allure.step("Открытие 'Уровни соревнований' и нажатие 'Добавить'", () -> {
            window.menuItemWithPath("Справочники", "Уровни соревнований").click();
            DialogFixture levelDialog = window.dialog(DialogMatcher.withTitle("Уровни соревнований").andShowing());
            sleep(1000);
            levelDialog.button("btnAdd").click();
        });

        Allure.step("Ввод Наименования уровня и сохранение", () -> {
            DialogFixture addLevelDialog = window.dialog(DialogMatcher.withTitle("Добавление нового уровня").andShowing());
            addLevelDialog.textBox("tfLevelName").setText("QA Тест Уровень");
            addLevelDialog.button("btnOk").click();
            sleep(1000);
            
            DialogFixture levelDialog = window.dialog(DialogMatcher.withTitle("Уровни соревнований").andShowing());
            levelDialog.table("tblLevel").cell("QA Тест Уровень").click();
            levelDialog.button("btnClose").click();
        });

        Allure.step("Открытие справочника 'Соревнования' и нажатие 'Добавить'", () -> {
            window.menuItemWithPath("Справочники", "Соревнования").click();
            DialogFixture eventDialog = window.dialog(DialogMatcher.withTitle("Соревнования").andShowing());
            sleep(1000);
            eventDialog.button("btnAdd").click();
        });

        Allure.step("Заполнение данных Соревнования и выбор созданного Уровня", () -> {
            DialogFixture addEventDialog = window.dialog(DialogMatcher.withTitle("Добавление нового соревнования").andShowing());
            addEventDialog.textBox("tfEventName").setText("QA Суперкубок");
            addEventDialog.textBox("tfLocation").setText("Санкт-Петербург");
            addEventDialog.textBox("tfYear").setText("2026");
            
            addEventDialog.comboBox("cbLevel").selectItem("QA Тест Уровень");
            addEventDialog.button("btnOk").click();
            sleep(1500);
        });

        Allure.step("Проверка появления Соревнования в таблице", () -> {
            DialogFixture eventDialog = window.dialog(DialogMatcher.withTitle("Соревнования").andShowing());
            eventDialog.table("tblEvent").cell("QA Суперкубок").click();
            eventDialog.button("btnClose").click();
        });
    }

    @Test
    @Story("Валидация пустых обязательных полей (REG-1.1)")
    public void testTeamAndLevelEmptyNameValidation() {
        Allure.step("Попытка создать команду без имени", () -> {
            window.button(JButtonMatcher.withText(".*КОМАНДЫ.*")).click();
            DialogFixture teamsDialog = window.dialog(DialogMatcher.withTitle("Команды").andShowing());
            sleep(1000);
            teamsDialog.button("btnAdd").click();
            
            DialogFixture addTeamDialog = teamsDialog.dialog(DialogMatcher.withTitle("Добавление новой команды").andShowing());
            addTeamDialog.textBox("tfCity").setText("Москва"); 
            addTeamDialog.button("btnOk").click();
            
            JOptionPaneFixture errorDialog = addTeamDialog.optionPane();
            errorDialog.requireErrorMessage().requireMessage("Название команды не может быть пустым.");
            errorDialog.okButton().click();
            
            addTeamDialog.button("btnCancel").click();
            teamsDialog.button("btnClose").click();
        });

        Allure.step("Попытка создать уровень соревнований без имени", () -> {
            window.menuItemWithPath("Справочники", "Уровни соревнований").click();
            DialogFixture levelDialog = window.dialog(DialogMatcher.withTitle("Уровни соревнований").andShowing());
            sleep(1000);
            levelDialog.button("btnAdd").click();
            
            DialogFixture addLevelDialog = levelDialog.dialog(DialogMatcher.withTitle("Добавление нового уровня").andShowing());
            addLevelDialog.button("btnOk").click(); 
            
            JOptionPaneFixture errorDialog = addLevelDialog.optionPane();
            errorDialog.requireErrorMessage().requireMessage("Название уровня не может быть пустым.");
            errorDialog.okButton().click();
            
            addLevelDialog.button("btnCancel").click();
            levelDialog.button("btnClose").click();
        });
    }

    @Test
    @Story("Валидация букв в числовых полях Игрока (REG-1.2)")
    public void testPlayerNumericFieldsValidation() {
        Allure.step("Открытие диалога добавления игрока", () -> {
            window.button(JButtonMatcher.withText(".*ИГРОКИ.*")).click();
            DialogFixture playersDialog = window.dialog(DialogMatcher.withTitle("Игроки").andShowing());
            sleep(1000);
            playersDialog.button("btnAdd").click();
        });

        Allure.step("Ввод букв в поля роста и веса и проверка перехвата ошибки", () -> {
            DialogFixture addPlayerDialog = window.dialog(DialogMatcher.withTitle("Добавление нового игрока").andShowing());
            addPlayerDialog.textBox("tfPlayerName").setText("Тест Ошибка");
            addPlayerDialog.textBox("tfHeight").setText("Сто"); 
            addPlayerDialog.textBox("tfWeight").setText("Девяносто"); 
            
            addPlayerDialog.button("btnOk").click();
            
            JOptionPaneFixture errorDialog = addPlayerDialog.optionPane();
            errorDialog.requireErrorMessage().requireMessage("Номер, рост и вес должны быть целыми числами.");
            errorDialog.okButton().click();
            
            addPlayerDialog.button("btnCancel").click();
            window.dialog(DialogMatcher.withTitle("Игроки").andShowing()).button("btnClose").click();
        });
    }

    @Test
    @Story("Валидация букв в поле 'Год' при создании соревнования (REG-1.2)")
    public void testEventYearValidationFails() {
        Allure.step("Открытие диалога добавления соревнования", () -> {
            window.menuItemWithPath("Справочники", "Соревнования").click();
            DialogFixture eventDialog = window.dialog(DialogMatcher.withTitle("Соревнования").andShowing());
            sleep(1000);
            eventDialog.button("btnAdd").click();
        });

        Allure.step("Ввод букв в поле года и проверка ошибки", () -> {
            DialogFixture addEventDialog = window.dialog(DialogMatcher.withTitle("Добавление нового соревнования").andShowing());
            addEventDialog.textBox("tfEventName").setText("Кубок UI Тестов");
            addEventDialog.textBox("tfYear").setText("Две тысячи"); 
            
            addEventDialog.button("btnOk").click();

            JOptionPaneFixture errorDialog = addEventDialog.optionPane();
            errorDialog.requireErrorMessage().requireMessage("Год должен быть числом."); 
            errorDialog.okButton().click();    

            addEventDialog.button("btnCancel").click(); 
            window.dialog(DialogMatcher.withTitle("Соревнования").andShowing()).button("btnClose").click();
        });
    }

    @Test
    @Story("Успешное редактирование и удаление записи (REG-1.3, REG-1.4)")
    public void testEditAndDeleteTeam() {
        Allure.step("Предусловие: Создание временной команды для теста", () -> {
            window.button(JButtonMatcher.withText(".*КОМАНДЫ.*")).click();
            DialogFixture teamsDialog = window.dialog(DialogMatcher.withTitle("Команды").andShowing());
            sleep(1000);
            teamsDialog.button("btnAdd").click();
            
            DialogFixture addTeamDialog = window.dialog(DialogMatcher.withTitle("Добавление новой команды").andShowing());
            addTeamDialog.textBox("tfTeamName").setText("Временная Команда");
            addTeamDialog.button("btnOk").click();
            sleep(1500); 
        });

        Allure.step("Редактирование созданной команды (PUT)", () -> {
            DialogFixture teamsDialog = window.dialog(DialogMatcher.withTitle("Команды").andShowing());
            teamsDialog.table("tblTeam").cell("Временная Команда").click(); 
            teamsDialog.button("btnEdit").click();
            
            DialogFixture editDialog = teamsDialog.dialog(DialogMatcher.withTitle("Редактирование команды").andShowing());
            editDialog.textBox("tfTeamName").deleteText().enterText("Обновленная Команда");
            editDialog.button("btnOk").click();
            sleep(1500);
            
            teamsDialog.table("tblTeam").cell("Обновленная Команда").click();
        });

        Allure.step("Удаление команды (DELETE)", () -> {
            DialogFixture teamsDialog = window.dialog(DialogMatcher.withTitle("Команды").andShowing());
            teamsDialog.table("tblTeam").cell("Обновленная Команда").click();
            teamsDialog.button("btnDelete").click();
            
            teamsDialog.optionPane().yesButton().click();
            sleep(1500);
            
            teamsDialog.button("btnClose").click();
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