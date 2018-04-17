d = beacondata;

for watch = 1:3
    figure;
    for beacon = 1:3
        w = d(d(:,1)== watch & d(:,4)== beacon, :); 
        x = w(:, 2);
        y = w(:, 6);
        y = smooth(y, 0.95);
        plot(x,y);
        if beacon == 1
            hold on
            grid on
        end
    end
    title(strcat('Watch: ', num2str(watch)));
    legend('Beacon 1', 'Beacon 2', 'Beacon 3');
    
end

