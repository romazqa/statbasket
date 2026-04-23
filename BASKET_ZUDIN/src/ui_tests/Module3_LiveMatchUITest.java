package ui_tests;

import com.MainWindow;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.*;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.DialogMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.junit.*;
import javax.swing.JFrame;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.Allure;

@Epic("UI Тестирование десктопного клиента")
@Feature("Модуль 3: Игровая логика")
public class Module3_LiveMatchUITest {
    private FrameFixture window;

    @Before
    public void setUp() {
        MainWindow frame = GuiActionRunner.execute(() -> new MainWindow());
        window = new FrameFixture(frame);
        window.show();
    }

    @Test
    @Story("Управление игровым таймером (Пауза и Продолжить)")
    public void testTimerPauseAndResume() {
        FrameFixture gameFrame = setupMatchViaWizard();

        Allure.step("Запуск таймера четверти", () -> {
            gameFrame.button("startQuarterButton").click();
            gameFrame.button("pauseTimerButton").requireEnabled();
            gameFrame.button("resumeTimerButton").requireDisabled();
        });

        Allure.step("Нажатие Паузы и проверка состояния интерфейса", () -> {
            gameFrame.button("pauseTimerButton").click();
            gameFrame.button("pauseTimerButton").requireDisabled();
            gameFrame.button("resumeTimerButton").requireEnabled();
            gameFrame.textBox("eventsTextArea").requireText("(?s).*Таймер остановлен.*");
        });

        Allure.step("Продолжение игры и проверка логов", () -> {
            gameFrame.button("resumeTimerButton").click();
            gameFrame.textBox("eventsTextArea").requireText("(?s).*Таймер запущен.*");
        });

        gameFrame.cleanUp();
    }

    @Test
    @Story("Взаимосвязанные события: Перехват и Потеря")
    public void testStealAndTurnoverChain() {
        FrameFixture gameFrame = setupMatchViaWizard();

        Allure.step("Запуск четверти и выбор игрока", () -> {
            gameFrame.button("startQuarterButton").click();
            gameFrame.button("playerBtnA0").click();
        });

        Allure.step("Нажатие 'перехват' и выбор соперника", () -> {
            gameFrame.button("перехват").click();
            DialogFixture opponentDialog = window.dialog(DialogMatcher.withTitle("Кто потерял мяч?"));
            opponentDialog.list().selectItem(0);
            opponentDialog.button(JButtonMatcher.withText("OK")).click();
        });

        Allure.step("Проверка логов на наличие обоих событий", () -> {
            String logs = gameFrame.textBox("eventsTextArea").text();
            Assert.assertTrue(logs.contains("перехват"));
            Assert.assertTrue(logs.contains("потеря"));
        });

        gameFrame.cleanUp();
    }

    @Test
    @Story("Механика замен игроков на площадке")
    public void testPlayerSubstitutionLogic() {
        FrameFixture gameFrame = setupMatchViaWizard();

        String initialPlayerName = gameFrame.button("playerBtnA0").text();

        Allure.step("Выбор игрока на площадке и открытие окна замен", () -> {
            gameFrame.button("playerBtnA0").click();
            gameFrame.button(JButtonMatcher.withText(".*Замена.*" + gameFrame.target().getTitle().split("vs")[0].trim().split(":")[1].trim() + ".*")).click();
        });

        Allure.step("Выбор игрока со скамейки запасных", () -> {
            DialogFixture subDialog = window.dialog(DialogMatcher.withTitle("Замена"));
            subDialog.list().selectItem(0); 
            subDialog.button(JButtonMatcher.withText("OK")).click();
        });

        Allure.step("Проверка изменения текста на кнопке и лога", () -> {
            Assert.assertNotEquals(initialPlayerName, gameFrame.button("playerBtnA0").text());
            gameFrame.textBox("eventsTextArea").requireText("(?s).*Замена: вышел.*");
        });

        gameFrame.cleanUp();
    }

    private FrameFixture setupMatchViaWizard() {
        final FrameFixture[] result = new FrameFixture[1];
        Allure.step("ПРЕДУСЛОВИЕ: Прохождение Wizard для запуска матча", () -> {
            window.button(JButtonMatcher.withText(".*НОВЫЙ МАТЧ.*")).click();
            DialogFixture wizard = window.dialog(DialogMatcher.withTitle("Новый матч").andShowing());
            sleep(1000);
            wizard.button("btnNext").click(); 
            wizard.button("btnNext").click(); 
            wizard.comboBox("team1ComboBox").selectItem(0);
            wizard.comboBox("team2ComboBox").selectItem(1);
            wizard.button("btnNext").click(); 
            wizard.button("btnNext").click(); 
            wizard.textBox("matchPlaygroundField").setText("Test-Arena");
            wizard.textBox("matchDateField").setText("01-01-2026");
            wizard.button("btnNext").click();

            result[0] = WindowFinder.findFrame(new org.assertj.swing.core.GenericTypeMatcher<JFrame>(JFrame.class) {
                @Override protected boolean isMatching(JFrame f) { return f.getTitle().contains("Ведение матча") && f.isShowing(); }
            }).using(window.robot());
        });
        return result[0];
    }

    @After
    public void tearDown() {
        if (window != null) window.cleanUp();
    }

    private void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {}
    }
}