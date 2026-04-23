package dk.siema.siemaexamproject.gui.util;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import javafx.fxml.FXMLLoader;

public class ViewFactory {

        private final ApplicationServices services;

        public ViewFactory(ApplicationServices services) {
            this.services = services;
        }

        public FXMLLoader createLoader(String fxmlPath) {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxmlPath)
            );

            loader.setControllerFactory(type -> {
                try {
                    Object controller = type.getDeclaredConstructor().newInstance();

                    if (controller instanceof ApplicationServicesAware aware) {
                        aware.setApplicationServices(services);
                        System.out.println("Injected ApplicationServices into: "+ type.getName());
                    }



                    return controller;
                } catch (Exception e) {
                    throw new RuntimeException("Could not create controller: " + type.getName(), e);
                }
            });



            return loader;
        }
    }

