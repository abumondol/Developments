d = beacondata;

for beacon = 1:3
    figure;
    for watch = 1:3
        w = d(d(:,1)== watch & d(:,4)== beacon, :); 
        x = w(:, 2);
        y = w(:, 6);
        y = smooth(y, 0.95);
        plot(x,y);
        if watch == 1
            hold on
            grid on
        end
    end
    title(strcat('Beacon: ', num2str(beacon)));
    legend('Watch 1', 'Watch 2', 'Watch 3');
    
end
