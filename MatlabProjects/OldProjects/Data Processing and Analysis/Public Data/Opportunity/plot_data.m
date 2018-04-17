clear; load('oppdata'); load('actchunks'); load('ico');
res = [];

hist_flag = true;
scatter_flag = true;

d = {};
k = 1;
if  hist_flag || scatter_flag                
    d = [];        
end

for sub = 1:4
    for sess = 1:6 
        chunks = actchunks{sub,sess}.chunks_lr;
        chunks = chunks(chunks(:, 3)==407 | chunks(:, 3)==409, :);       
        %chunks = chunks(chunks(:, 3)==408 & chunks(:, 4)~=512, :);       
        
        a = oppdata{sub,sess}.accel;                    
        g = oppdata{sub,sess}.grav;  
        
        chunk_count = size(chunks, 1);
        fprintf('%d, %d:: %d\n', sub, sess, chunk_count);        
        
        if chunk_count ==0
            continue
        end
        
        duration = (chunks(:,2)-chunks(:,1))/30;
        diff = (chunks(2:end, 1)-chunks(1:end-1, 2))/30;
        diff = [0;diff];
        res = [res; zeros(chunk_count, 1)+sub, zeros(chunk_count, 1)+sess, chunks, duration, diff];
        %continue
        
%        figure
        for i = 1:chunk_count
            s = chunks(i,1);
            e = chunks(i, 2);            
            m = round((s+e)/2);
            
            if  hist_flag || scatter_flag                
                d = [d; g(m, :)];
                %d = [d; g(s:e, :)];
            else
                d{k} = g(s-50:e+50, :);
                k = k+1;
            end
            
            plot(g(s-110:e+110, :));
            legend('X', 'Y', 'Z');
            ylim([-1,1]);
            xlabel('Sample Count');
            ylabel('Amplitude');
            waitforbuttonpress
            %plot(a(s-100:e+100, :));
            %legend('AX', 'AY', 'AZ');
            %waitforbuttonpress
        end        
    end
end

    if hist_flag
        bin_count = 50;
        [mu, sigma] = normfit(d)
        
        figure
        histogram(d(:,1), bin_count);
        xlim([-1, 1]);
        title('X axis');
        xlabel('g_x');

        figure
        histogram(d(:,2), bin_count);
        xlim([-1, 1]);
        title('Y axis');
        xlabel('g_y');

        figure
        histogram(d(:,3), bin_count);
        xlim([-1, 1]);  
        title('Z axis');
        xlabel('g_z');
        
    else        
        if scatter_flag            
            d = {d};            
        end        
        ico_number = 2;
        sphere_flag = true;
        cell_no_flag = false;        
        ttl = 'Bite/Sip Moments';
        plot_data_sphere_multi_chunks(d, ttl, sphere_flag, ico, ico_number, cell_no_flag, scatter_flag);
    end


