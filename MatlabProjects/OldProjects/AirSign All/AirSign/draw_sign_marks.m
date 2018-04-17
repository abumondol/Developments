function draw_sign_marks(signature, sensor)

graph_title = strcat(signature.subject_name,'-', num2str(signature.serial));
movavg_points = 5;

if sensor==1
    data = signature.raw_data;
    data = data(data(:,2)== 1, 3:5); 
    data = [data, sqrt(sum(data.*data,2))]; 
    data = tsmovavg(data, 'e', movavg_points, 1);    
    data(1:movavg_points-1, :) = 0;    
    data = zscore(data);
    mp = signature.acc_mp;
    graph_title = strcat('Acceleration: ', graph_title);
else
    data = signature.raw_data;
    data = data(data(:,2)== 4, 3:5); 
    data = [data, sqrt(sum(data.*data,2))];    
    mp = signature.gyro_mp;
    graph_title = strcat('Gyroscope: ', graph_title);
end

figure
plot(data(:,4)); % magnitude
grid on
hold on
plot(data(:,1)); % x axis
plot(data(:,2)); % y axis
plot(data(:,3)); % x axis

if ~isempty(mp)
    scatter(mp, data(mp,1),'bd');
    scatter(mp, data(mp,2),'bd');
    scatter(mp, data(mp,3),'bd');
    scatter(mp, data(mp,4),'bd');    
end

title(graph_title);
legend('Magnitude','X-axis','Y-axis','Z-axis');
hold off

end