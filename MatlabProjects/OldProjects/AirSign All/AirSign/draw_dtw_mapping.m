function draw_dtw_mapping(sign1, sign2, dtw_paths, sensor, axis)

legend1 = strcat('Sign:',num2str(sign1.serial));
legend2 = strcat('Sign:',num2str(sign2.serial));
graph_title = strcat('Sensor:', num2str(sensor), ',', 'Axis:',  num2str(axis), ',', legend1, ',', legend2);
if sensor ==1    
    data1 = sign1.acc(:,axis);    
    data2 = sign2.acc(:,axis);
    path = dtw_paths(axis).path;
    if axis ==1        
        pp1 = sign1.acc_ppx;
        pp2 = sign2.acc_ppx;
    elseif axis ==2
        pp1 = sign1.acc_ppy;
        pp2 = sign2.acc_ppy;
    elseif axis ==3
        pp1 = sign1.acc_ppz;
        pp2 = sign2.acc_ppz;
    else 
        pp1 = sign1.acc_ppm;
        pp2 = sign2.acc_ppm;
    end           
else
    data1 = sign1.gyro(:,axis);    
    data2 = sign2.gyro(:,axis);
    path = dtw_paths(axis+4).path;
    if axis ==1        
        pp1 = sign1.gyro_ppx;
        pp2 = sign2.gyro_ppx;
    elseif axis ==2
        pp1 = sign1.gyro_ppy;
        pp2 = sign2.gyro_ppy;
    elseif axis ==3
        pp1 = sign1.gyro_ppz;
        pp2 = sign2.gyro_ppz;
    else 
        pp1 = sign1.gyro_ppm;
        pp2 = sign2.gyro_ppm;
    end    
end

[p1, p2] = find_points(path, pp1, pp2);

figure
plot(data1);
grid on
hold on
plot(data2);
scatter(pp1(:,1),pp1(:,2));
scatter(pp2(:,1),pp2(:,2));
plot(p1, p2, ':')
legend(legend1, legend2);
title(graph_title);

hold off
end

function [x1, x2] = find_points(w, pp1, pp2)      
   a = pp1(w(:,1), :);
   b = pp2(w(:,2), :);
   x1 = [a(:,1) b(:, 1)]'; 
   x2 = [a(:,2) b(:, 2)]';
end

