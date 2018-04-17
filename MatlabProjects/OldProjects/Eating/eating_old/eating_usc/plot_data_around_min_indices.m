load('data_usc');
load('min_indices');
data = data_usc;
offset = 5;

for sub = 2:2    
    d = data(sub).accel;    
    %d = smooth(d(:,2:4), 0.90);
    len = size(d, 1);

    ai = data(sub).accel_annot_indices; 
    mi = min_indices(sub).indices;
    for i = 1:400:len        
        close all;
        figure('units','normalized','outerposition',[0 0 1 1]);
        %figure              
        
        x = i:i+500; % timestamps                
        y = d(x,2);        
        plot(x, y);        
        
        xlim([i, i+500]);
        ylim([-15, 15]);
        
        ttl = strcat('sub:', num2str(sub)); title(ttl);
        hold on; grid on;        
        plot([x(1);x(end)], [0; 0], 'k'); %plot zero line                        
        
        a = ai( ai(:,1) >= i & ai(:,1) <= i+500, 1 ); 
        m = mi( mi(:,1) >= i & mi(:,1) <= i+500, 1);         
        
        scatter(a, d(a, 2), 'rd'); % annotated mark point
        %scatter(m, d(m,2), 'bd'); % annotated mark point
        scatter(m-48, d(m-48,2), 'bx'); % annotated mark point
        scatter(m+32, d(m+32,2), 'bo'); % annotated mark point
        
        waitforbuttonpress            
    end
                
end


