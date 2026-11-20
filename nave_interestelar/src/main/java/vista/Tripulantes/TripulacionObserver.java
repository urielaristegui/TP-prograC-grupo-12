package vista.Tripulantes;

import modelo.Tripulacion.Tripulante;
import java.util.List;

public interface TripulacionObserver {
    void onTripulacionActualizada(List<Tripulante> tripulantes);
}
