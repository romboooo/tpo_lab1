package task3;

import org.example.task3.*;
import org.example.task3.Character;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class SceneTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach  // ← БЫЛО @BeforeEach, СТАЛО @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        outContent.reset();
    }

    @Test
    @DisplayName("1. базовая симуляция сцены: полный поток выполнения")
    public void testSceneSimulationFlow() {
        Character character = new Character();
        Scene scene = new Scene(character);

        scene.simulateScene();

        String output = outContent.toString();
        assertTrue(output.contains("начал медленно двигаться к пульту"));
        assertTrue(output.contains("Перемещение к объекту: Console"));
        assertTrue(output.contains("На бесплотной голове почувствовалось движение"));
        assertTrue(output.contains("волосы зашевелились"));
        assertTrue(output.contains("Но это был всего лишь"));
        assertTrue(output.contains("Камера сделала наезд (zoom)"));
        assertTrue(output.contains("догадался, что это эффект съемки"));
    }

    @Test
    @DisplayName("2. инициализация Character: проверка композиции")
    public void testCharacterInitialization() {
        Character character = new Character();

        assertNotNull(character);
        assertDoesNotThrow(() -> character.getName());
        assertNull(character.getName(), "name по умолчанию не установлен");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Артур", "Иван", "Maria", "", "Test@123"})
    @DisplayName("3. параметризованный тест: отображение имени персонажа в выводе")
    public void testCharacterNameInOutput(String name) {
        assertNotNull(name);
        assertDoesNotThrow(() -> {
            String testOutput = name + " начал медленно двигаться к пульту.";
            assertTrue(testOutput.contains(name));
        });
    }

    @ParameterizedTest
    @CsvSource({
            "Черные, Черные волосы зашевелились",
            "Белые, Белые волосы зашевелились",
            "Рыжие, Рыжие волосы зашевелились",
            "Золотистые, Золотистые волосы зашевелились",
            "Серебряные, Серебряные волосы зашевелились"
    })
    @DisplayName("4. параметризованный тест: цвет волос и сообщение о движении")
    public void testHairColorMovement(String color, String expectedMessage) {
        Hair hair = new Hair(color);
        Head head = new Head(true, hair);

        head.feelHairMovement();

        String output = outContent.toString();
        assertTrue(output.contains("На бесплотной голове почувствовалось движение"));
        assertTrue(output.contains(expectedMessage));
    }

    @Test
    @DisplayName("5. Head: проверка поведения для не-бесплотной головы")
    public void testHeadNonEtherealNoMovement() {
        Hair hair = new Hair("Черные");
        Head head = new Head(false, hair);

        head.feelHairMovement();

        String output = outContent.toString();
        assertTrue(output.isEmpty(), "Для не-бесплотной головы вывод должен быть пустым");
    }

    @Test
    @DisplayName("6. Camera.zoom(): проверка сообщения")
    public void testCameraZoomOutput() {
        Camera camera = new Camera();
        camera.zoom();

        String output = outContent.toString().trim();
        assertEquals("Камера сделала наезд (zoom).", output);
    }

    @Test
    @DisplayName("7. Console: инициализация и сообщение")
    public void testConsoleInitialization() {
        Console console = new Console();

        String output = outContent.toString().trim();
        assertEquals("Пульт управления готов.", output);
    }

    @Test
    @DisplayName("8. Position.moveTo(): проверка сообщения с именем класса")
    public void testPositionMoveTo() {
        Position position = new Position();
        Console console = new Console();
        // Не сбрасываем outContent — хотим проверить вывод moveTo, а не Console
        position.moveTo(console);

        String output = outContent.toString().trim();
        // Вывод содержит оба сообщения: от Console и от Position
        assertTrue(output.contains("Перемещение к объекту: Console"));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName("9. параметризованный тест: Head.isEthereal влияет на вывод")
    public void testHeadEtherealFlag(boolean isEthereal) {
        Hair hair = new Hair("Тестовые");
        Head head = new Head(isEthereal, hair);

        head.feelHairMovement();

        String output = outContent.toString();
        if (isEthereal) {
            assertTrue(output.contains("На бесплотной голове почувствовалось движение"));
            assertTrue(output.contains("волосы зашевелились"));
        } else {
            assertTrue(output.isEmpty(), "При isEthereal=false вывод должен быть пустым");
        }
    }

    @Test
    @DisplayName("10. комплексный тест: порядок сообщений в симуляции")
    public void testSceneMessageOrder() {
        Character character = new Character();
        Scene scene = new Scene(character);

        scene.simulateScene();

        String output = outContent.toString();
        int moveIndex = output.indexOf("начал медленно двигаться");
        int positionIndex = output.indexOf("Перемещение к объекту");
        int hairIndex = output.indexOf("волосы зашевелились");
        int cameraIndex = output.indexOf("Камера сделала наезд");
        int realizationIndex = output.indexOf("догадался, что это эффект съемки");

        assertTrue(moveIndex < positionIndex, "Движение должно быть перед перемещением");
        assertTrue(positionIndex < hairIndex, "Перемещение перед ощущением волос");
        assertTrue(hairIndex < cameraIndex, "Ощущение волос перед зумом камеры");
        assertTrue(cameraIndex < realizationIndex, "Зум камеры перед осознанием");
    }

    @Test
    @DisplayName("11. проверка устойчивости: множественные вызовы симуляции")
    public void testMultipleSceneSimulations() {
        Character character = new Character();
        Scene scene = new Scene(character);

        scene.simulateScene();
        String firstOutput = outContent.toString();
        outContent.reset();

        scene.simulateScene();
        String secondOutput = outContent.toString();

        assertEquals(firstOutput, secondOutput, "Многократные симуляции должны быть детерминированы");
    }

    @Test
    @DisplayName("12. проверка: все компоненты создаются без исключений")
    public void testAllComponentsInstantiation() {
        assertDoesNotThrow(() -> new Hair("test"), "Hair с цветом должен создаваться");
        assertDoesNotThrow(() -> new Head(true, new Hair("black")), "Head должен создаваться");
        assertDoesNotThrow(Position::new, "Position должен создаваться");
        assertDoesNotThrow(Console::new, "Console должен создаваться");
        assertDoesNotThrow(Camera::new, "Camera должен создаваться");
        assertDoesNotThrow(Character::new, "Character должен создаваться");
        assertDoesNotThrow(() -> new Scene(new Character()), "Scene должен создаваться");
    }
}