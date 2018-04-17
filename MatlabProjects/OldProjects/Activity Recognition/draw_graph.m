function draw_graph()
load('data');
load('dict');
len = length(data);

fprintf('Drawing graph. Total data: %d\n', len);
for i = 1:len    
    d = data(i).grav_quat(:, 1:2);
    l = length(d);
    
    figure
    plot(d(:,1));
    grid on
    hold on
    plot(d(:,2));
    legend('X-Value', 'Y-Value');
    title('X-Y of gravity(quaternion)');
    hold off
  
    figure
    plot(d(:,1), d(:,2));
    axis([-1 1 -1 1]);
    title('X-Y of gravity(quaternion)');
    xlabel('X');
    ylabel('Y');
    hold off
    
    figure
    %d = data(i).quantized_xy(:, 1:2);
    indices = data(i).query_indices;
    points = [indices, d(indices,:)];
    plot(d(:,1));
    grid on
    hold on
    plot(d(:,2));
    scatter(points(:,1), points(:, 2));
    scatter(points(:,1), points(:, 3));
    legend('X-Value', 'Y-Value');
    title('X-Y of gravity(quaternion) quantized');
    hold off
  
    figure
    plot(d(:,1), d(:,2));
    axis([-1 1 -1 1]);
    grid on
    hold on
    scatter(points(:,2), points(:, 3));
    title('X-Y of gravity (quaternion) quantized');
    xlabel('X');
    ylabel('Y');
    hold off
    
end

fprintf('Drawing graph done.\n');
end

