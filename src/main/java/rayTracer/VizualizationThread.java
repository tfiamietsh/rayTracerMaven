package rayTracer;

import javax.swing.*;
import java.awt.*;

public class VizualizationThread extends Thread {
	VizualizationThread(String script, Dimension resolution,
			JProgressBar progressBar, JImage viewImage, JTextArea textArea, JMenuItem menuExecute) {
		this.script = script;
		this.resolution = resolution;
		this.progressBar = progressBar;
		this.image = viewImage;
		this.textArea = textArea;
		this.menuExecute = menuExecute;
	}
	
	@Override
    public void run() {
		//	пытаемся выполнить сценарий
		errors = Script.tryToExecuteScript(script, resolution, progressBar);

		//	если не удалось выводим ошибки и показываем приветственное изображение
		if (errors != "") {
			textArea.setText(errors);
			image.load("src/main/resources/default.png");
		}
		//	если удалось - очищаем ошибки и показываем результат визуализации
		else {
			textArea.setText("");
			image.load("src/main/resources/RenderedScene.png");
		}
		
		//	после завершения визуализации снова делаем кнопку
		//	Выполнить сценарий активной и скрываем прогрессбар
		menuExecute.setEnabled(true);
		progressBar.setVisible(false);
    }
	
	private String script, errors;
	private JProgressBar progressBar;
	private Dimension resolution;
	private JImage image;
	private JTextArea textArea;
	private JMenuItem menuExecute;
}
