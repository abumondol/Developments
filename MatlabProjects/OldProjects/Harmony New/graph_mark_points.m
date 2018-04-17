
sub_count = length(hw);
for i=2:sub_count
    episode_count = length(hw(i).data);
    
    for j= 1:episode_count
        acc = hw(i).data(j).acc;
        a = hw(i).data(j).a;
        b = hw(i).data(j).b;
        acc_mag = sqrt(sum(acc.*acc,2));
        
        figure;
        plot(acc(:,1));
        hold on
        plot(acc(:,2));
        plot(acc(:,3));
        plot(acc_mag);        
        plot([a;a],[-30;30]);
        plot([b;b],[-30;30]);
        
        legend('X','Y','Z','M');        
        title(hw(i).data(j).file_name);    
        
    end
end