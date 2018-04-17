function draw_sign_peaks(signature, sensor)

graph_title = strcat(signature.subject_name,'-', num2str(signature.serial));
if sensor==1
    data = signature.acc;    
    ppx = signature.acc_ppx;
    ppy = signature.acc_ppy;
    ppz = signature.acc_ppz;
    ppm = signature.acc_ppm;
    graph_title = strcat('Acceleration: ', graph_title);
else
    data = signature.gyro;    
    ppx = signature.gyro_ppx;
    ppy = signature.gyro_ppy;
    ppz = signature.gyro_ppz;
    ppm = signature.gyro_ppm;
    graph_title = strcat('Gyroscope: ', graph_title);
end

figure
plot(data(:,4)); % magnitude
grid on
hold on
plot(data(:,1)); % x axis
plot(data(:,2)); % y axis
plot(data(:,3)); % x axis

scatter(ppm(:,1),ppm(:,2));
scatter(ppx(:,1),ppx(:,2));
scatter(ppy(:,1),ppy(:,2));
scatter(ppz(:,1),ppz(:,2));

title(graph_title);
legend('Magnitude','X-axis','Y-axis','Z-axis');
hold off

end