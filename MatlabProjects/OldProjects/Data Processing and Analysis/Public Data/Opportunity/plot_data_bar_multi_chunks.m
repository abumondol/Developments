function plot_data_bar_multi_chunks(data, ttl, bin_counts)    
    d = data{1};
    figure
    histogram(d(:,1), bin_counts);
    xlim([-1,1]);
    title(strcat(ttl, '-X-1'));
    
    figure
    histogram(d(:,2), bin_counts);
    xlim([-1,1]);
    title(strcat(ttl, '-Y-1'));
    
    figure
    histogram(d(:,3), bin_counts);
    xlim([-1,1]);
    title(strcat(ttl, '-Z-1'));
    
    count = length(data);    
    for i=2:count
        d = data{i};
        figure
        histogram(d(:,1), bin_counts);
        title(strcat(ttl, '-X-', num2str(i)) );

        figure
        histogram(d(:,2), bin_counts);
        title(strcat(ttl, '-Y-', num2str(i)) );

        figure
        histogram(d(:,3), bin_counts);
        title(strcat(ttl, '-Z-', num2str(i)) );
            
        %waitforbuttonpress
    end
    
end