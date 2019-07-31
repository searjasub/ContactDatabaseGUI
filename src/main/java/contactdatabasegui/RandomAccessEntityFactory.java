package contactdatabasegui;

public interface RandomAccessEntityFactory <Z extends RandomAccessEntity> {

    Z createInstance();
}
