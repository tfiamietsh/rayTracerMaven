package rayTracer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

//	класс для вывода изображения, наследник элемента JLabel библиотеки swing
@SuppressWarnings("serial")
public class JImage extends JLabel {
	public JImage() { super(); }
	
	public JImage(String filename) {
		super();
		load(filename);
	}
	
	//	загружаем изображение
	public void load(String filename) {
		try {
			bufferedImage = ImageIO.read(new File(filename));
		} catch (IOException e) {
			String dir = System.getProperty("user.dir");
			
			JOptionPane.showMessageDialog(null, "Файл " + filename +
				" не найден в директории " + dir, "Ошибка", JOptionPane.ERROR_MESSAGE);
		}
		//	если успешно удалось загрузить изображение, перерисовываем его
		if (bufferedImage != null)
			repaint();
	}
		
	@Override
    public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		//	при вызове метода рисования подгоняем размер изображения под размеры элемента JComponent
		Image scaledImage = bufferedImage.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_SMOOTH);
		g.drawImage(scaledImage, 0, 0, null);
	}
	
	BufferedImage bufferedImage;
}