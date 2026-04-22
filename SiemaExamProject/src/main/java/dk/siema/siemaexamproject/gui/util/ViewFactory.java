package dk.siema.siemaexamproject.gui.util;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import javafx.fxml.FXMLLoader;

public class ViewFactory {

        private final ApplicationServices services;

        public ViewFactory(ApplicationServices services) {
            this.services = services;
        }

        public FXMLLoader createLoader(ViewPath viewPath) {
            FXMLLoader loader = new FXMLLoader(
                    viewPath.getURL()
            );

            loader.setControllerFactory(type -> {
                try {
                    Object controller = type.getDeclaredConstructor().newInstance();

                    if (controller instanceof ApplicationServicesAware aware) {
                        aware.setApplicationServices(services);
                    }

                    return controller;
                } catch (Exception e) {
                    throw new RuntimeException("Could not create controller: " + type.getName(), e);
                }
            });

            return loader;
        }
    }

