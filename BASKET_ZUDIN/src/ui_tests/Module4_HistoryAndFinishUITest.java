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
@Feature("Модуль 4: Завершение и История")
public class Module4_HistoryAndFinishUITest {
    private FrameFixture window;

    @Before
    public void setUp() {
        MainWindow frame = GuiActionRunner.execute(() -> new MainWindow());
        window = new FrameFixture(frame);
        window.show();
    }

    @Test
    @Story("Сохранение матча на сервер и формирование отчета (E2E)")
    public void testFinishMatchAndSave() {
        final FrameFixture[] gameFrameWrapper = new FrameFixture[1];

        Allure.step("Прохождение Мастера создания матча", () -> {
            window.button(JButtonMatcher.withText(".*НОВЫЙ МАТЧ.*")).click();
            DialogFixture wizard = window.dialog(DialogMatcher.withTitle("Новый матч").andShowing());
            sleep(1000);
            wizard.button("btnNext").click(); 
            wizard.button("btnNext").click(); 
            wizard.comboBox("team1ComboBox").selectItem(0);
            wizard.comboBox("team2ComboBox").selectItem(1);
            wizard.button("btnNext").click(); 
            wizard.button("btnNext").click(); 
            wizard.textBox("matchPlaygroundField").setText("Финал");
            wizard.textBox("matchDateField").setText("01-01-2026");
            wizard.button("btnNext").click();

            gameFrameWrapper[0] = WindowFinder.findFrame(new org.assertj.swing.core.GenericTypeMatcher<JFrame>(JFrame.class) {
                @Override protected boolean isMatching(JFrame f) { return f.getTitle().contains("Ведение матча") && f.isShowing(); }
            }).using(window.robot());
        });

        FrameFixture gameFrame = gameFrameWrapper[0];

        Allure.step("Симуляция прохождения 4-х четвертей игры", () -> {
            for (int q = 1; q <= 4; q++) {
                gameFrame.comboBox("quarterComboBox").selectItem(String.valueOf(q));
                gameFrame.button("startQuarterButton").click();
                if (q == 1) {
                    gameFrame.button("playerBtnA0").click();
                    gameFrame.button("2 очка").click(); 
                }
                gameFrame.button("endQuarterButton").click();
            }
        });

        Allure.step("Завершение матча и сохранение на сервер", () -> {
            gameFrame.button("finishMatchButton").click();
            gameFrame.optionPane().yesButton().click(); 
            sleep(3000);
            gameFrame.optionPane().okButton().click(); 
        });

        Allure.step("Закрытие окна предпросмотра отчета", () -> {
            DialogFixture reportDialog = WindowFinder.findDialog(DialogMatcher.withTitle("Отчет о матче")).withTimeout(5000).using(window.robot());
            reportDialog.button(JButtonMatcher.withText("Закрыть")).click();
        });

        Allure.step("Проверка успешного закрытия окна матча", () -> {
            gameFrame.requireNotVisible();
        });
    }

    @Test
    @Story("Просмотр и удаление завершенного матча из Истории")
    public void testDeleteMatchFromHistory() {
        Allure.step("Открытие списка матчей", () -> {
            window.button(JButtonMatcher.withText(".*СПИСОК МАТЧЕЙ.*")).click();
        });

        Allure.step("Удаление первого матча в таблице", () -> {
            DialogFixture historyDialog = window.dialog(DialogMatcher.withTitle("Матчи").andShowing());
            sleep(2000);
            JTableFixture table = historyDialog.table();
            
            if (table.rowCount() > 0) {
                int initialCount = table.rowCount();
                table.selectRows(0); 
                historyDialog.button(JButtonMatcher.withText("Удалить")).click(); 
                historyDialog.optionPane().yesButton().click(); 

                sleep(2000);
                Assert.assertEquals("Проверка, что количество строк уменьшилось", initialCount - 1, table.rowCount());
            }
            historyDialog.button(JButtonMatcher.withText("Закрыть")).click();
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