function draw_for_speed(signatures, sensor, axis)
% draw one axis of all signatures in one graph
%graph_title = strcat(signature.subject_name,'- serial:', num2str(signature.serial),', axis:',num2str(axis));
graph_title='X axis';
if sensor==1    
    graph_title = strcat('Raw Acceleration: ', graph_title);
    len = length(signatures);
    data = signatures(1).acc_nz(:,axis);
    figure
    plot(data); % magnitude
    hold on
    for i = 2:len
         data = signatures(i).acc_nz(:,axis);   
         plot(data);
    end
    title(graph_title);
    legend('Fast', 'Average','Slow');
    title(graph_title);
    
    
    graph_title = strcat('Peak Acceleration: ', graph_title);
    len = length(signatures);
    data = signatures(1).acc_ppx(:,2);
    figure
    plot(data); % magnitude
    hold on
    for i = 2:len
         data = signatures(i).acc_ppx(:,2); 
         plot(data);
    end
    title(graph_title);
    legend('Fast','Average','Slow');
    title(graph_title);
    
else
    graph_title = strcat('Raw Gyroscope: ', graph_title);
    len = length(signatures);
    data = signatures(1).gyro_nz(:,axis);
    figure
    plot(data); % magnitude
    hold on
    for i = 2:len
         data = signatures(i).gyro_nz(:,axis);   
         plot(data);
    end
    title(graph_title);
    legend('Average','Fast','Slow');
    title(graph_title);
end

% scatter(ppm(:,1),ppm(:,2));
% scatter(ppx(:,1),ppx(:,2));
% scatter(ppy(:,1),ppy(:,2));
% scatter(ppz(:,1),ppz(:,2));

end