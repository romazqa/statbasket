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
@Feature("Модуль 2: Мастер создания матча")
public class Module2_WizardUITest {
    private FrameFixture window;

    @Before
    public void setUp() {
        MainWindow frame = GuiActionRunner.execute(() -> new MainWindow());
        window = new FrameFixture(frame);
        window.show();
    }

    @Test
    @Story("Проверка бизнес-логики (запрет выбора одинаковых команд)")
    public void testWizardSameTeamsError() {
        Allure.step("Открытие Мастера нового матча", () -> {
            window.button(JButtonMatcher.withText(".*НОВЫЙ МАТЧ.*")).click();
        });

        Allure.step("Прохождение этапов Уровень и Соревнование", () -> {
            DialogFixture wizard = window.dialog(DialogMatcher.withTitle("Новый матч"));
            sleep(1000);
            wizard.button("btnNext").click(); 
            wizard.button("btnNext").click(); 
        });

        Allure.step("Выбор одинаковой команды в обоих списках", () -> {
            DialogFixture wizard = window.dialog(DialogMatcher.withTitle("Новый матч"));
            wizard.comboBox("team1ComboBox").selectItem(0);
            wizard.comboBox("team2ComboBox").selectItem(0);
            wizard.button("btnNext").click();
        });

        Allure.step("Проверка появления ошибки 'Команды не могут быть одинаковыми'", () -> {
            DialogFixture wizard = window.dialog(DialogMatcher.withTitle("Новый матч"));
            wizard.optionPane().requireErrorMessage()
                  .requireMessage("Команда 1 и Команда 2 не могут быть одинаковыми!");
            wizard.optionPane().okButton().click();
            wizard.button(JButtonMatcher.withText("Отмена")).click();
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