#!/Users/jaime/.rvm/rubies/ruby-2.1.2/bin/ruby
require 'socket'

server = TCPServer.new(9090)

loop do

      Thread.start(server.accept) do |session|
        begin
          from = "#{session.peeraddr[2]}"
          puts "Accepting connection from #{from}..."
          puts "[#{from}] Connection accepted from '#{session.peeraddr[2]}'."

          while true
            input = session.readline.encode("UTF-8").scan(/[[:print:]]/).join
            puts "Read: #{input}"
          end

          session.close rescue nil
          puts "[#{from}] Connection closed."

        rescue EOFError
          puts "[#{from}] Connection lost."

        rescue Errno::ECONNRESET
          puts "[#{from}] Connection reset."

        rescue Errno::ENOTCONN
          puts "[#{from}] Transport endpoint is not connected."

        rescue Errno::EPIPE
          puts "[#{from}] Broken pipe."

        rescue => ex
          puts "[#{from}] #{ex.message}"          
        end

      end

end
